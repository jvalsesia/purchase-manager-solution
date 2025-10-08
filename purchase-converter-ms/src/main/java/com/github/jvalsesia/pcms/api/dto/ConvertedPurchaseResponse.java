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
@Schema(name = "ConvertedPurchaseResponse", description = "Schema to hold the Purchase Response after saved in database")
public class ConvertedPurchaseResponse {
    @Schema(description = "The unique id passed to GET method in order to retrieve the Purchase by id")
    private UUID id;
    @Schema(description = "Purchase Description")
    private String description;
    @Schema(description = "Purchase Transaction Date")
    private LocalDate transactionDate;
    @Schema(description = "Purchase Original Amount in USD")
    private BigDecimal originalAmount;
    @Schema(description = "Purchase Exchanged Rate returned by Treasury API")
    private BigDecimal exchangeRate;
    @Schema(description = "Purchase Converted Amount")
    private BigDecimal convertedAmount;
    @Schema(description = "Purchase Target Currency")
    private String targetCurrency;
}