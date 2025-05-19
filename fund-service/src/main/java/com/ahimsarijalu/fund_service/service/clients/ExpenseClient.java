package com.ahimsarijalu.fund_service.service.clients;

import com.ahimsarijalu.fund_service.dtos.ExpenseDTO;
import com.ahimsarijalu.fund_service.utils.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "expense-service", url = "http://expense-service:8080")
public interface ExpenseClient {
    @GetMapping("/expense/{fundId}")
    ApiResponse<List<ExpenseDTO>> getExpenseByFundId(@PathVariable String fundId);
}
