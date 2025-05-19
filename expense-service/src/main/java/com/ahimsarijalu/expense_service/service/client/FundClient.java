package com.ahimsarijalu.expense_service.service.client;

import com.ahimsarijalu.expense_service.dtos.FundDTO;
import com.ahimsarijalu.expense_service.utils.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "fund-service", url = "http://fund-service:8080")
public interface FundClient {
    @GetMapping("/fund/{userId}/{fundId}")
    ApiResponse<FundDTO> getFundById(@PathVariable String userId, @PathVariable String fundId);

    @PutMapping("/fund/{userId}/{fundId}")
    ApiResponse<FundDTO> updateFund(@PathVariable String userId, @PathVariable String fundId, @RequestBody FundDTO fundDTO);
}
