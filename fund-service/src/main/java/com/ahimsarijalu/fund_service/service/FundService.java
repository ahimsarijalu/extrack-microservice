package com.ahimsarijalu.fund_service.service;

import com.ahimsarijalu.fund_service.FundRepository;
import com.ahimsarijalu.fund_service.dtos.FundDTO;
import com.ahimsarijalu.fund_service.model.Fund;
import com.ahimsarijalu.fund_service.service.clients.ExpenseClient;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ahimsarijalu.fund_service.FundUtils.mapFundToDTO;
import static com.ahimsarijalu.fund_service.FundUtils.mapFundToDTOWithoutExpenses;
import static com.ahimsarijalu.fund_service.utils.MapperUtil.mapDTOToEntity;


@Service
public class FundService {

    @Autowired
    private FundRepository fundRepository;

    @Autowired
    private ExpenseClient expenseClient;

    public FundDTO saveFund(String userId, FundDTO fundDTO) {
//        User user = userRepository.findById(UUID.fromString(userId))
//                .orElseThrow(() -> new EntityNotFoundException("User not found with this id: " + userId));

        Fund fund = mapDTOToEntity(fundDTO, Fund.class);
        fund.setUserId(UUID.fromString(userId));
//        fund.setUser(user);
        return mapFundToDTOWithoutExpenses(fundRepository.save(fund));
    }

    public List<FundDTO> getAllFundsByUserId(String userId) {
        return fundRepository.findAllByUserId(UUID.fromString(userId))
                .stream()
                .map((fund) -> mapFundToDTO(fund, expenseClient.getExpenseByFundId(fund.getId().toString()).getData()))
                .collect(Collectors.toList());
    }

    public FundDTO getFundByUserId(String userId) {
        return mapFundToDTO(fundRepository.findByUserId(UUID.fromString(userId)), expenseClient.getExpenseByFundId(UUID.fromString(userId).toString()).getData());
    }

    public FundDTO getFundById(String userId, String fundId) {
//        userRepository.findById(UUID.fromString(userId))
//                .orElseThrow(() -> new EntityNotFoundException("User not found with this id: " + userId));

        return mapFundToDTO(fundRepository.findById(UUID.fromString(fundId))
                        .orElseThrow(() -> new EntityNotFoundException("Fund not found with this id: " + fundId)),
                expenseClient.getExpenseByFundId(fundId).getData());

//                mapFundToDTOWithoutExpenses(fundRepository.findById(UUID.fromString(fundId))
//                .orElseThrow(() -> new EntityNotFoundException("Fund not found with this id: " + fundId)));
    }

    public FundDTO updateFund(String userId, String fundId, FundDTO fundDTO) {
//        userRepository.findById(UUID.fromString(userId))
//                .orElseThrow(() -> new EntityNotFoundException("User not found with this id: " + userId));
        Fund fund = fundRepository.findById(UUID.fromString(fundId))
                .orElseThrow(() -> new EntityNotFoundException("User not found with this id: " + fundId));

        if (fundDTO.getName() != null) {
            fund.setName(fundDTO.getName());
        }

        if (fundDTO.getBalance() != null) {
            fund.setBalance(fundDTO.getBalance());
        }
        return mapFundToDTOWithoutExpenses(fundRepository.save(fund));
    }

    public void deleteFund(String userId, String fundId) {
//        userRepository.findById(UUID.fromString(userId))
//                .orElseThrow(() -> new EntityNotFoundException("User not found with this id: " + userId));
        Fund fund = fundRepository.findById(UUID.fromString(fundId))
                .orElseThrow(() -> new EntityNotFoundException("Fund not found with this id: " + fundId));
        fundRepository.delete(fund);
    }

}
