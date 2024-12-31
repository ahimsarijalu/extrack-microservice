package com.ahimsarijalu.expense_service.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TopCategoryDTO {
    private String topCategory;
    private Long totalAmount;

    public TopCategoryDTO(String topCategory, Long totalAmount) {
        this.topCategory = topCategory;
        this.totalAmount = totalAmount;
    }
}
