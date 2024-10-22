package com.example.trainingsapp.financialtransaktioncategory;

import com.example.trainingsapp.financialtransaktioncategory.api.FinancialTransactionCategoryService;
import com.example.trainingsapp.financialtransaktioncategory.api.dto.FinancialTransactionCategoryCreateDTO;
import com.example.trainingsapp.financialtransaktioncategory.api.dto.FinancialTransactionCategoryDTO;
import com.example.trainingsapp.financialtransaktioncategory.api.dto.FinancialTransactionCategoryDetailedDTO;
import com.example.trainingsapp.user.api.UserService;
import com.example.trainingsapp.user.api.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/categories")
public class FinancialTransactionCategoryController {

    @Autowired
    private UserService userService;

    @Autowired
    private FinancialTransactionCategoryService financialTransactionCategoryService;

    @PostMapping()
    public ResponseEntity<FinancialTransactionCategoryDTO> createCategory(
            @Valid @RequestBody FinancialTransactionCategoryCreateDTO categoryCreateDTO, Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        FinancialTransactionCategoryDTO categoryDTO =
                financialTransactionCategoryService.createCategory(categoryCreateDTO, userId);

        return new ResponseEntity<>(categoryDTO, HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialTransactionCategoryDetailedDTO> getFinancialCategoryById(
            @NotNull @Min(1) @PathVariable Long id, Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userID = user.getId();

        FinancialTransactionCategoryDetailedDTO categoryDetailedDTO = financialTransactionCategoryService
                .findFinancialTransactionCategoryForUser(id, userID);

        return new ResponseEntity<>(categoryDetailedDTO, HttpStatus.OK);

    }


    @GetMapping()
    public ResponseEntity<List<FinancialTransactionCategoryDTO>> getFinancialTransactionCategories(
            Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userID = user.getId();

    }



}
