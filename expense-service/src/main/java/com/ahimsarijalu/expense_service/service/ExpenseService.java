package com.ahimsarijalu.expense_service.service;

import com.ahimsarijalu.expense_service.ExpenseRepository;
import com.ahimsarijalu.expense_service.dtos.ExpenseDTO;
import com.ahimsarijalu.expense_service.dtos.FundDTO;
import com.ahimsarijalu.expense_service.model.Expense;
import com.ahimsarijalu.expense_service.service.client.FundClient;
import com.ahimsarijalu.expense_service.utils.ExpenseUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ahimsarijalu.expense_service.utils.ExpenseUtil.mapExpenseToDTO;
import static com.ahimsarijalu.expense_service.utils.MapperUtil.mapDTOToEntity;


@Slf4j
@Service
public class ExpenseService {

    @Autowired
    private FundClient fundClient;

    @Autowired
    private ExpenseRepository expenseRepository;


//    public List<ExpenseDTO> getAllExpenses() {
//        List<ExpenseDTO> expenses = expenseRepository.findAll()
//                .stream()
//                .map(ExpenseUtil::mapExpenseToDTO)
//                .collect(Collectors.toList());
//
//        if (expenses.isEmpty()) {
//            throw new EntityNotFoundException("No Expenses found");
//        }
//
//        return expenses;
//    }

    public List<ExpenseDTO> getAllExpenseByFundId(String fundId) {
        return expenseRepository.findAllByFundId(UUID.fromString(fundId))
                .stream().map(ExpenseUtil::mapExpenseToDTO).collect(Collectors.toList());
    }

    public ExpenseDTO getExpenseById(String id) {
        return expenseRepository.findById(UUID.fromString(id))
                .map(ExpenseUtil::mapExpenseToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Expense with id " + id + " not found"));
    }

    public ExpenseDTO saveExpense(ExpenseDTO expenseDTO) {
        FundDTO fund = fundClient.getFundById(expenseDTO.getUserId(), expenseDTO.getFundId()).getData();

        long totalAmount = fund.getBalance() - expenseDTO.getAmount();
        if (totalAmount < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative");
        }

        fund.setBalance(totalAmount);
        Expense expense = mapDTOToEntity(expenseDTO, Expense.class);
        expense.setUserId(UUID.fromString(expenseDTO.getUserId()));
        expense.setFundId(UUID.fromString(expenseDTO.getFundId()));
//        expense.setId(UUID.randomUUID());

        try {
            fund = fundClient.updateFund(expenseDTO.getUserId(), fund.getId(), fund).getData();
        } catch (Exception e) {
            log.error("Error updating fund balance", e);
            throw new RuntimeException("Error updating fund balance");
        }
        expense = expenseRepository.save(expense);
        return mapExpenseToDTO(expense);
    }

    public void updateExpense(String id, ExpenseDTO expenseDTO) {
        Expense expense = expenseRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Expense with id " + id + " not found"));

        if (expenseDTO.getTitle() != null) {
            expense.setTitle(expenseDTO.getTitle());
        }
        if (expenseDTO.getDescription() != null) {
            expense.setDescription(expenseDTO.getDescription());
        }
        if (expenseDTO.getAmount() != null) {
            FundDTO fund = fundClient.getFundById(expenseDTO.getUserId(), expenseDTO.getFundId()).getData();
            if (expenseDTO.getAmount() > expense.getAmount()) {
                long diff = expenseDTO.getAmount() - expense.getAmount();
                long totalAmount = fund.getBalance() - diff;
                if (totalAmount < 0) {
                    throw new IllegalArgumentException("Total amount cannot be negative");
                }
                fund.setBalance(totalAmount);
            } else {
                long diff = expense.getAmount() - expenseDTO.getAmount();
                long totalAmount = fund.getBalance() + diff;
                fund.setBalance(totalAmount);
            }
            expense.setAmount(expenseDTO.getAmount());
            fundClient.updateFund(fund.getUserId(), fund.getId(), fund);
        }
        if (expenseDTO.getCategory() != null) {
            expense.setCategory(expenseDTO.getCategory());
        }
        if (expenseDTO.getUserId() != null) {
            expense.setExpenseDate(expenseDTO.getExpenseDate());
        }
        mapExpenseToDTO(expenseRepository.save(expense));
    }

    @Transactional
    public void deleteExpense(String id) {
        Expense expense = expenseRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Expense with id " + id + " not found"));

        FundDTO fund = fundClient.getFundById(expense.getUserId().toString(), expense.getFundId().toString()).getData();
        log.info("FundDTO: {}", fund);
        long totalAmount = fund.getBalance() + expense.getAmount();
        if (totalAmount < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative");
        }

        try {
            fund.setBalance(totalAmount);
            fundClient.updateFund(fund.getUserId(), fund.getId(), fund);
        } catch (Exception e) {
            log.error("Error updating fund balance", e);
            throw new RuntimeException("Error updating fund balance");
        }

        expenseRepository.delete(expense);
    }

//    public List<ExpenseDTO> getAllExpensesByCategory(Category category) {
//        return expenseRepository.findAllByCategory(category)
//                .stream()
//                .map(ExpenseUtil::mapExpenseToDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<ExpenseDTO> searchByTitleAndDescription(String query) {
//        return expenseRepository.findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query)
//                .stream()
//                .map(ExpenseUtil::mapExpenseToDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<ExpenseDTO> getAllExpenseByUserId(String userId) {
//        return expenseRepository.findAllByUserId(UUID.fromString(userId))
//                .stream()
//                .map(ExpenseUtil::mapExpenseToDTO)
//                .collect(Collectors.toList());
//    }
//
//    public TopCategoryDTO findTopCategoryByUserId(String userId) {
//        TopCategoryDTO topCategory = expenseRepository.findTopCategoryByUserId(UUID.fromString(userId));
//
//        if (topCategory == null) {
//            throw new EntityNotFoundException("Top category not found for user ID: " + userId);
//        }
//
//        return topCategory;
//    }

}
