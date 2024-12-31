package com.ahimsarijalu.expense_service;

import com.ahimsarijalu.expense_service.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findAllByFundId(UUID fundId);

}