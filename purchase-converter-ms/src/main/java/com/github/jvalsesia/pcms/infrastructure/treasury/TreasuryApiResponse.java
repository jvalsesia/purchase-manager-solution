package com.github.jvalsesia.pcms.infrastructure.treasury;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class TreasuryApiResponse {
    private List<ExchangeRateData> data;

    @Data
    public static class ExchangeRateData {
        @JsonProperty("effective_date")
        private LocalDate effectiveDate;

        @JsonProperty("country")
        private String country;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("country_currency_desc")
        private String countryCurrencyDesc;

        @JsonProperty("exchange_rate")
        private BigDecimal exchangeRate;
    }
}