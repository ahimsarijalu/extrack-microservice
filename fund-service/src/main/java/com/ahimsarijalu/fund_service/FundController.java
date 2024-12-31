package com.ahimsarijalu.fund_service;

import com.ahimsarijalu.fund_service.dtos.FundDTO;
import com.ahimsarijalu.fund_service.service.FundService;
import com.ahimsarijalu.fund_service.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ahimsarijalu.fund_service.utils.MapperUtil.mapToApiResponse;


@Slf4j
@RestController
@RequestMapping("/fund")
public class FundController {

    @Autowired
    private final FundService fundService;


    public FundController(FundService fundService) {
        this.fundService = fundService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Fund Service";
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<FundDTO>> createFundByUserId(@PathVariable String userId, @Valid @RequestBody FundDTO fundDTO) {
        FundDTO createdFund = fundService.saveFund(userId, fundDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToApiResponse(HttpStatus.CREATED.value(), true, "Fund created successfully", createdFund));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<FundDTO>>> getFundByUserId(@PathVariable String userId) {
        List<FundDTO> funds = fundService.getAllFundsByUserId(userId);
        return ResponseEntity.ok(mapToApiResponse(HttpStatus.OK.value(), true, "Funds fetched successfully", funds));
    }

    @GetMapping("/{userId}/{id}")
    public ResponseEntity<ApiResponse<FundDTO>> getFundById(@PathVariable String userId, @PathVariable String id) {
        FundDTO fund = fundService.getFundById(userId, id);
        return ResponseEntity.ok(mapToApiResponse(HttpStatus.OK.value(), true, "Fund fetched successfully", fund));
    }

    @PutMapping("/{userId}/{id}")
    public ResponseEntity<ApiResponse<FundDTO>> updateFund(@PathVariable String userId, @PathVariable String id, @RequestBody FundDTO fundDTO) {
        FundDTO updatedFund = fundService.updateFund(userId, id, fundDTO);
        return ResponseEntity.ok(mapToApiResponse(HttpStatus.OK.value(), true, "Fund updated successfully", updatedFund));
    }

    @DeleteMapping("/{userId}/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFund(@PathVariable String userId, @PathVariable String id) {
        fundService.deleteFund(userId, id);
        return ResponseEntity.ok(mapToApiResponse(HttpStatus.OK.value(), true, "Fund deleted successfully", null));
    }
}
