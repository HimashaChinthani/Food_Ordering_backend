import React, { useEffect, useMemo, useState } from "react";

const API_BASE = "http://localhost:8082/api/payments";
const STORAGE_KEY = "pendingPayment";

// Save checkout context before redirecting to PayPal.
export function persistPendingPayment({ orderDbId, orderIds, amount }) {
  const payload = {
    orderDbId: orderDbId ?? null,
    orderIds: Array.isArray(orderIds) ? orderIds : [],
    amount: typeof amount === "number" ? amount : Number(amount)
  };

  localStorage.setItem(STORAGE_KEY, JSON.stringify(payload));
}

function readPendingPayment() {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) return null;

  try {
    return JSON.parse(raw);
  } catch (_err) {
    return null;
  }
}

export default function PayPalSuccessPage() {
  const [state, setState] = useState({ status: "loading", message: "Finalizing payment..." });

  const params = useMemo(() => new URLSearchParams(window.location.search), []);
  const token = params.get("token");
  const paymentStatusFromBackend = params.get("payment");

  useEffect(() => {
    async function run() {
      // New backend callback flow already captured payment and redirected with status.
      if (paymentStatusFromBackend) {
        if (paymentStatusFromBackend === "completed") {
          setState({ status: "success", message: "Payment completed successfully." });
        } else {
          const reason = params.get("reason") || "capture_error";
          setState({ status: "error", message: `Payment failed (${reason}).` });
        }
        return;
      }

      // Fallback flow: frontend captures after PayPal redirect.
      const pending = readPendingPayment();
      if (!token || !pending) {
        setState({ status: "error", message: "Missing payment token or pending checkout context." });
        return;
      }

      const amount = Number(pending.amount);
      const orderDbId = pending.orderDbId || (pending.orderIds && pending.orderIds[0]);

      if (!orderDbId || !amount || amount <= 0) {
        setState({ status: "error", message: "Invalid order or amount in local storage." });
        return;
      }

      try {
        const query = new URLSearchParams({
          token,
          amount: String(amount),
          orderDbId: String(orderDbId)
        });

        const response = await fetch(`${API_BASE}/paypal/capture?${query.toString()}`, {
          method: "POST"
        });

        if (!response.ok) {
          const txt = await response.text();
          throw new Error(txt || `Capture failed with status ${response.status}`);
        }

        localStorage.removeItem(STORAGE_KEY);
        setState({ status: "success", message: "Payment captured and order updated." });
      } catch (err) {
        setState({ status: "error", message: err.message || "Capture request failed." });
      }
    }

    run();
  }, [token, paymentStatusFromBackend, params]);

  return (
    <div style={{ padding: "2rem", fontFamily: "Arial, sans-serif" }}>
      <h2>Payment Status</h2>
      <p>{state.message}</p>
      {state.status === "success" && <p>You can now view your paid orders.</p>}
      {state.status === "error" && <p>Please retry or contact support.</p>}
    </div>
  );
}

