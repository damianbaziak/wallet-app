package com.example.trainingsapp.financialtransaktioncategory;

import com.example.trainingsapp.TestUtils;
import com.example.trainingsapp.authorization.JwtAuthorizationFilter;
import com.example.trainingsapp.authorization.api.MyUserDetailsService;
import com.example.trainingsapp.authorization.webtoken.JwtService;
import com.example.trainingsapp.financialtransaktioncategory.api.FinancialTransactionCategoryService;
import com.example.trainingsapp.financialtransaktioncategory.api.dto.FinancialTransactionCategoryDTO;
import com.example.trainingsapp.financialtransaktioncategory.api.dto.FinancialTransactionCategoryDetailedDTO;
import com.example.trainingsapp.financialtransaktioncategory.impl.FinancialTransactionCategoryServiceImpl;
import com.example.trainingsapp.general.exception.AppRuntimeException;
import com.example.trainingsapp.general.exception.ErrorCode;
import com.example.trainingsapp.general.exception.ErrorStrategy;
import com.example.trainingsapp.user.api.UserRepository;
import com.example.trainingsapp.user.api.model.User;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Optional;

import static com.example.trainingsapp.financialtransaction.api.model.FinancialTransactionType.EXPENSE;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FinancialTransactionCategoryController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {FinancialTransactionCategoryServiceImpl.class}),
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtService.class, MyUserDetailsService.class, JwtAuthorizationFilter.class,}))
class FinancialTransactionCategoryGetControllerTest {
    private static final Long CATEGORY_ID_1L = 1L;
    private static final Long USER_ID_1L = 1L;
    private static final String USER_EMAIL = "example@email.com";
    private static final Instant DATE = Instant.parse("2024-12-22T14:30:00.500Z");
    private static final String EXAMPLE_CATEGORY_NAME = "Example category name";

    @MockBean
    UserRepository userRepository;
    @MockBean
    FinancialTransactionCategoryService financialTransactionCategoryService;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ErrorStrategy errorStrategy;

    @Test
    @DisplayName("Should returns financial transaction category detailed DTO and status OK when category exists")
    @WithMockUser(username = USER_EMAIL)
    void getFinancialCategoryById_categoryExist_returnsFTCategoryDetailedDTO() throws Exception {
        // given
        User user = TestUtils.createUserForTest();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        FinancialTransactionCategoryDTO financialTransactionCategoryDTO =
                TestUtils.createFinancialTransactionCategoryDTOForTest(EXPENSE, USER_ID_1L);
        financialTransactionCategoryDTO.setCreationDate(DATE);

        BigInteger numberOfTransactions = new BigInteger("4");

        FinancialTransactionCategoryDetailedDTO financialTransactionCategoryDetailedDTO =
                new FinancialTransactionCategoryDetailedDTO(financialTransactionCategoryDTO, numberOfTransactions);

        when(financialTransactionCategoryService.findFinancialTransactionCategoryForUser(CATEGORY_ID_1L, USER_ID_1L))
                .thenReturn(financialTransactionCategoryDetailedDTO);

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/{id}", CATEGORY_ID_1L));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.financialTransactionCategoryDTO.id").value(CATEGORY_ID_1L))
                .andExpect(jsonPath("$.financialTransactionCategoryDTO.name").value(EXAMPLE_CATEGORY_NAME))
                .andExpect(jsonPath("$.financialTransactionCategoryDTO.type").value(String.valueOf(EXPENSE)))
                .andExpect(jsonPath("$.financialTransactionCategoryDTO.creationDate").value(String.valueOf(DATE)))
                .andExpect(jsonPath("$.financialTransactionCategoryDTO.userId").value(USER_ID_1L))
                .andExpect(jsonPath("$.financialTransactionCounter").value(4));
        verify(financialTransactionCategoryService, times(1))
                .findFinancialTransactionCategoryForUser(CATEGORY_ID_1L, USER_ID_1L);

    }

    @Test
    @DisplayName("Should returns status 404, when financial transaction category with given ID is not found")
    @WithMockUser(username = USER_EMAIL)
    void getFinancialCategoryById_categoryNotExist_returnsNotFound() throws Exception {
        // given
        User user = TestUtils.createUserForTest();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        doThrow(new AppRuntimeException(ErrorCode.FTC001, "Category not found")).when(financialTransactionCategoryService)
                .findFinancialTransactionCategoryForUser(CATEGORY_ID_1L, USER_ID_1L);

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(
                "/api/categories/{id}", CATEGORY_ID_1L));

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category not found"));
        verify(financialTransactionCategoryService, times(1))
                .findFinancialTransactionCategoryForUser(CATEGORY_ID_1L, USER_ID_1L);

    }

    @Test
    @DisplayName("Should returns UNAUTHORIZED when user is not authenticated")
    void getFinancialCategoryById_userNotFound_returnsUnauthorized() throws Exception {
        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(
                "/api/categories/{id}", CATEGORY_ID_1L));

        // then
        result
                .andExpect(status().isUnauthorized());
        verify(financialTransactionCategoryService, times(0))
                .findFinancialTransactionCategoryForUser(CATEGORY_ID_1L, USER_ID_1L);

    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should returns BAD_REQUEST when given category ID is 0 (violates @Min constraint)")
    void getFinancialCategoryById_categoryIdIsZero_returnsConstraintViolationException() throws Exception {
        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(
                "/api/categories/{id}", 0));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", CoreMatchers.is(ErrorCode.TEA003.getBusinessStatus())))
                .andExpect(jsonPath("$.message", CoreMatchers.is(errorStrategy.returnExceptionMessage(
                        ErrorCode.TEA003.getBusinessMessage()))))
                .andExpect(jsonPath("$.statusCode", CoreMatchers.is(ErrorCode.TEA003.getHttpStatusCode())));

        verify(financialTransactionCategoryService, times(0))
                .findFinancialTransactionCategoryForUser(0L, USER_ID_1L);

    }


}