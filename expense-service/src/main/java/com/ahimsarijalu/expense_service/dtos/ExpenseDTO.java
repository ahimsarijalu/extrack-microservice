package com.ahimsarijalu.expense_service.dtos;


import com.ahimsarijalu.expense_service.model.Category;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExpenseDTO {
    private String id;

    @NotNull(message = "Title must not be null or empty")
    private String title;

    @NotNull(message = "Description must not be null or empty")
    private String description;

    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be greater than zero")
    private Long amount;

    @NotNull(message = "Category must not be null")
    private Category category;

    @NotNull(message = "Expense date must not be null")
    private LocalDateTime expenseDate;

    @NotNull(message = "User ID must not be null or empty")
    private String userId;

    @NotNull(message = "Fund ID must not be null or empty")
    private String fundId;
}
