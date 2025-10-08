package com.github.jvalsesia.pcms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PurchaseResponse", description = "Schema to hold the Purchase Response after saved in database")
public class PurchaseResponse {
    @Schema(description = "The unique id generated automatically")
    private UUID id;
    @Schema(description = "Purchase description")
    private String description;
    @Schema(description = "Purchase transaction date")
    private LocalDate transactionDate;
    @Schema(description = "Purchase transaction amount")
    private BigDecimal purchaseAmount;
}