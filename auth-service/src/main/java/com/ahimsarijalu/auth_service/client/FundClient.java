package com.ahimsarijalu.auth_service.client;

import com.ahimsarijalu.auth_service.dto.FundDTO;
import com.ahimsarijalu.auth_service.utils.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "fund-service")
public interface FundClient {
    @GetMapping("/fund/{userId}")
    ApiResponse<List<FundDTO>> getFundsByUserId(@PathVariable String userId);
}
