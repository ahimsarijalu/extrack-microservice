package com.ahimsarijalu.expense_service.utils;

import com.ahimsarijalu.expense_service.dtos.ExpenseDTO;
import com.ahimsarijalu.expense_service.model.Expense;
import org.springframework.beans.BeanUtils;

public class ExpenseUtil {
    public static ExpenseDTO mapExpenseToDTO(Expense expense) {
        ExpenseDTO expenseDTO = new ExpenseDTO();
        BeanUtils.copyProperties(expense, expenseDTO);
        expenseDTO.setId(expense.getId().toString());
        expenseDTO.setUserId(expense.getUserId().toString());
        expenseDTO.setFundId(expense.getFundId().toString());
        return expenseDTO;
    }
}
