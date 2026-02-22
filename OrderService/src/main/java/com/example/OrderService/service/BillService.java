package com.example.OrderService.service;

import com.example.OrderService.dto.OrderDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class BillService {

    private final ObjectMapper mapper = new ObjectMapper();

    public String generateCustomerBill(OrderDto order) {
        List<Map<String, Object>> items = parseItems(order);
        double subtotal = items.stream()
                .mapToDouble(it -> {
                    double price = toDouble(it.get("price"));
                    double qty = toDouble(it.get("qty"));
                    return price * (qty <= 0 ? 1 : qty);
                }).sum();

        StringBuilder sb = new StringBuilder();
        sb.append("================================\n");
        sb.append("          🍽 FOODIEHUB           \n");
        sb.append("================================\n");
        sb.append("         🧾 CUSTOMER BILL       \n");
        sb.append("--------------------------------\n");
        sb.append(" Order ID   : ").append(nullSafe(order.getOrderId())).append("\n");
        sb.append(" Customer   : ").append(nullSafe(order.getCustomerName())).append("\n");
        sb.append("--------------------------------\n");
        sb.append(" Items\n");
        sb.append("--------------------------------\n");

        if (items.isEmpty()) {
            // fallback to raw items string if present
            String raw = safeToString(order.getItems());
            if (raw.isEmpty()) {
                sb.append(" (no items)\n");
            } else {
                sb.append(" ").append(raw).append("\n");
            }
        } else {
            // columns: name (max 18) | qty(3) | unit(10) | line(11)
            for (Map<String, Object> it : items) {
                String name = truncate(nullSafe(it.get("name")), 18);
                String qty = rightPadNumber((int) toDouble(it.get("qty")), 3);
                String unit = rightPadMoney(toDouble(it.get("price")), 10);
                String line = rightPadMoney(toDouble(it.get("price")) * (toDouble(it.get("qty")) <= 0 ? 1 : toDouble(it.get("qty"))), 11);
                sb.append(" ").append(padRight(name, 18))
                        .append(" ").append(qty)
                        .append(" ").append(padLeft(unit, 10))
                        .append(" ").append(padLeft(line, 11))
                        .append("\n");
            }
        }

        sb.append("--------------------------------\n");
        sb.append(" TOTAL      : ").append(formatMoney(subtotal)).append("\n");
        sb.append("================================\n");
        sb.append("   THANK YOU FOR ORDERING ❤     \n");
        sb.append("        Visit Again!            \n");
        sb.append("================================");
        return sb.toString();
    }

    public String generateDriverBill(OrderDto order) {
        StringBuilder sb = new StringBuilder();
        sb.append("================================\n");
        sb.append("          🍽 FOODIEHUB           \n");
        sb.append("================================\n");
        sb.append("          🚚 DRIVER BILL        \n");
        sb.append("--------------------------------\n");
        sb.append(" Order ID   : ").append(nullSafe(order.getOrderId())).append("\n");
        sb.append("--------------------------------\n");
        sb.append(" Please deliver safely\n");
        sb.append("================================");
        return sb.toString();
    }

    // --- helpers ---

    private List<Map<String, Object>> parseItems(OrderDto order) {
        Object raw = order.getItems();
        if (raw == null) return new ArrayList<>();
        try {
            if (raw instanceof String) {
                String s = ((String) raw).trim();
                if (s.isEmpty()) return new ArrayList<>();
                // try parse JSON array -> List<Map>
                return mapper.readValue(s, new TypeReference<List<Map<String, Object>>>() {});
            } else if (raw instanceof List) {
                //noinspection unchecked
                return (List<Map<String, Object>>) raw;
            } else {
                // attempt to convert using ObjectMapper
                return mapper.convertValue(raw, new TypeReference<List<Map<String, Object>>>() {});
            }
        } catch (Exception e) {
            // parsing failed - return empty and let caller use raw fallback
            return new ArrayList<>();
        }
    }

    private static double toDouble(Object o) {
        if (o == null) return 0;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try { return Double.parseDouble(String.valueOf(o)); } catch (Exception e) { return 0; }
    }

    private static String nullSafe(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private static String safeToString(Object o) {
        if (o == null) return "";
        String s = String.valueOf(o).trim();
        return s.isEmpty() ? "" : s;
    }

    private static String formatMoney(double v) {
        // use simple ₨ prefix with 2 decimals
        return String.format("₨%.2f", v);
    }

    private static String rightPadMoney(double v, int width) {
        return formatMoney(v);
    }

    private static String padLeft(String s, int len) {
        if (s == null) s = "";
        if (s.length() >= len) return s.substring(s.length() - len);
        return String.format("%" + len + "s", s);
    }

    private static String padRight(String s, int len) {
        if (s == null) s = "";
        if (s.length() >= len) return s.substring(0, len);
        return String.format("%-" + len + "s", s);
    }

    private static String truncate(String s, int len) {
        if (s == null) return "";
        return s.length() <= len ? s : s.substring(0, len - 1) + "…";
    }

    private static String rightPadNumber(int n, int width) {
        String s = String.valueOf(n);
        if (s.length() >= width) return s;
        return padLeft(s, width);
    }
}
