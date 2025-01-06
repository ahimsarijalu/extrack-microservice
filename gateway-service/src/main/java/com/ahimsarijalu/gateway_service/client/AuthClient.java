package com.ahimsarijalu.gateway_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service")
public interface AuthClient {
    @GetMapping("/auth/validate")
    String validateToken(@RequestParam("token") String token);
}
