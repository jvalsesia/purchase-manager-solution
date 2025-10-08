package com.github.jvalsesia.pcms.api.dto;

import com.github.jvalsesia.pcms.api.validator.ValidDateFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseRequest {

    @NotBlank(message = "Description is required")
    @Size(max = 50, message = "Description must not exceed 50 characters")
    @Schema(
            description = "Purchase description", example = "Office Supplies"
    )
    private String description;


    @NotNull(message = "Transaction date is required")
    @PastOrPresent(message = "Transaction date must not be in the future")
    @ValidDateFormat
    @Schema(
            description = "Purchase transaction date, the valid format is yyyy-MM-dd", example = "2025-10-02"
    )
    private LocalDate transactionDate;


    @NotNull(message = "Purchase amount is required")
    @Positive(message = "Purchase amount must be positive")
    @DecimalMin(value = "0.01", message = "Purchase amount must be at least 0.01")
    @Schema(
            description = "Purchase transaction amount, must be positive greater than ZERO", example = "385.91"
    )
    private BigDecimal purchaseAmount;
}