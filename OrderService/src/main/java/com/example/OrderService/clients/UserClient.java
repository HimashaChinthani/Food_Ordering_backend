package com.example.OrderService.clients;

import com.example.OrderService.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        url = "http://localhost:8080"
)
public interface UserClient {

    @GetMapping("/api/v1/getuser/{id}")
    UserDTO getUserById(@PathVariable("id") String id);
}
