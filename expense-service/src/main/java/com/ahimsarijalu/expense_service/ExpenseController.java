package com.ahimsarijalu.expense_service;

import com.ahimsarijalu.expense_service.dtos.ExpenseDTO;
import com.ahimsarijalu.expense_service.service.ExpenseService;
import com.ahimsarijalu.expense_service.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ahimsarijalu.expense_service.utils.MapperUtil.mapToApiResponse;

@Slf4j
@RestController
@RequestMapping("/expense")
public class ExpenseController {

    @Autowired
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/hello")
    public String hello() {
        log.info("Log: Hello from Expense Service");
        return "Hello from Expense Service";
    }

    @GetMapping("/{fundId}")
    public ResponseEntity<ApiResponse<List<ExpenseDTO>>> getAllExpensesByFundId(@PathVariable String fundId) {
        List<ExpenseDTO> expenses = expenseService.getAllExpenseByFundId(fundId);
        return ResponseEntity.ok(mapToApiResponse(HttpStatus.OK.value(), true, "Expenses fetched successfully", expenses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseDTO>> createExpense(@Valid @RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO responseDTO = expenseService.saveExpense(expenseDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToApiResponse(HttpStatus.CREATED.value(), true, "Expense saved successfully", responseDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseDTO>> updateExpense(@PathVariable String id, @RequestBody ExpenseDTO expenseDTO) {
        expenseService.updateExpense(id, expenseDTO);
        ExpenseDTO updatedExpense = expenseService.getExpenseById(id);
        return ResponseEntity.status(HttpStatus.OK).body(mapToApiResponse(HttpStatus.OK.value(), true, "Expense updated successfully", updatedExpense));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable String id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.status(HttpStatus.OK).body(mapToApiResponse(HttpStatus.OK.value(), true, "Expense deleted successfully", null));
    }
//
//    @GetMapping("/category/{category}")
//    public ResponseEntity<ApiResponse<List<ExpenseDTO>>> getAllExpensesByCategory(@PathVariable String category) {
//        List<ExpenseDTO> expenses = expenseService.getAllExpensesByCategory(Category.valueOf(category.toUpperCase()));
//        return ResponseEntity.status(HttpStatus.OK).body(mapToApiResponse(
//                HttpStatus.OK.value(),
//                true,
//                "Expenses by category of " + category.toUpperCase() + " fetched successfully",
//                expenses
//        ));
//    }
//
//    @GetMapping("/category/user/{userId}")
//    public ResponseEntity<ApiResponse<TopCategoryDTO>> getTopCategoryByUserId(@PathVariable String userId) {
//        TopCategoryDTO expenses = expenseService.findTopCategoryByUserId(userId);
//        return ResponseEntity.status(HttpStatus.OK).body(mapToApiResponse(
//                HttpStatus.OK.value(),
//                true,
//                "Expenses by category of " + userId + " fetched successfully",
//                expenses
//        ));
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<ApiResponse<List<ExpenseDTO>>> searchByTitleAndDescription(@RequestParam String query) {
//        List<ExpenseDTO> expenses = expenseService.searchByTitleAndDescription(query);
//        return ResponseEntity.status(HttpStatus.OK).body(mapToApiResponse(
//                HttpStatus.OK.value(),
//                true,
//                "Expenses searched successfully",
//                expenses
//        ));
//    }
}
