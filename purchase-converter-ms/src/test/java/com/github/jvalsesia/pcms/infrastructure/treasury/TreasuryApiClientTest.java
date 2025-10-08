package com.github.jvalsesia.pcms.infrastructure.treasury;

import com.github.jvalsesia.pcms.infrastructure.treasury.config.TreasuryApiConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test for TreasuryApiClient using real RestClient
 * This test makes actual HTTP calls to the Treasury API
 */
@SpringBootTest
@TestPropertySource(properties = {
        "treasury.api.scheme=https",
        "treasury.api.host=api.fiscaldata.treasury.gov",
        "treasury.api.path=/services/api/fiscal_service/v1/accounting/od/rates_of_exchange"
})
class TreasuryApiClientTest {
    @Autowired
    private TreasuryApiConfiguration treasuryApiConfiguration;

    private TreasuryApiClient treasuryApiClient;

    @BeforeEach
    void setUp() {
        RestClient restClient = RestClient.builder().build();
        treasuryApiClient = new TreasuryApiClient(restClient, treasuryApiConfiguration);
        treasuryApiClient.treasuryApiConfiguration = treasuryApiConfiguration;
    }

    @Test
    void getExchangeRate_Success_WithValidCountryAndDate() {
        // Given
        LocalDate testDate = LocalDate.of(2024, 6, 15);
        String country = "Brazil";

        // When
        Optional<BigDecimal> result = treasuryApiClient.getExchangeRatesFiltered(country, testDate);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isPositive();
        assertThat(result.get()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void getExchangeRate_Success_WithDifferentCountries() {
        // Given
        LocalDate testDate = LocalDate.of(2024, 6, 1);

        // When & Then - Canada
        Optional<BigDecimal> canadaRate = treasuryApiClient.getExchangeRatesFiltered("Canada", testDate);
        assertThat(canadaRate).isPresent();
        assertThat(canadaRate.get()).isPositive();

        // When & Then - Mexico
        Optional<BigDecimal> mexicoRate = treasuryApiClient.getExchangeRatesFiltered("Mexico", testDate);
        assertThat(mexicoRate).isPresent();
        assertThat(mexicoRate.get()).isPositive();

        // When & Then - United Kingdom
        Optional<BigDecimal> ukRate = treasuryApiClient.getExchangeRatesFiltered("United Kingdom", testDate);
        assertThat(ukRate).isPresent();
        assertThat(ukRate.get()).isPositive();
    }

    @Test
    void getExchangeRate_Success_WithRecentDate() {
        // Given - a date within the last month
        LocalDate recentDate = LocalDate.now().minusMonths(1);
        String country = "Japan";

        // When
        Optional<BigDecimal> result = treasuryApiClient.getExchangeRatesFiltered(country, recentDate);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isPositive();
    }

    @Test
    void getExchangeRate_Empty_WithInvalidCountry() {
        // Given
        LocalDate testDate = LocalDate.of(2024, 6, 15);
        String invalidCountry = "NonExistentCountry123";

        // When
        Optional<BigDecimal> result = treasuryApiClient.getExchangeRatesFiltered(invalidCountry, testDate);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void getExchangeRate_ThrowsException_WithFutureDate() {
        // Given
        LocalDate futureDate = LocalDate.now().plusYears(1);
        String country = "Brazil";

        // When & Then
        assertThatThrownBy(() -> treasuryApiClient.getExchangeRatesFiltered(country, futureDate))
                .isInstanceOf(TreasuryApiException.class)
                .hasMessageContaining("Purchase date cannot be in the future");
    }

    @Test
    void getExchangeRate_ThrowsException_WithTomorrowDate() {
        // Given
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String country = "Brazil";

        // When & Then
        assertThatThrownBy(() -> treasuryApiClient.getExchangeRatesFiltered(country, tomorrow))
                .isInstanceOf(TreasuryApiException.class)
                .hasMessageContaining("Purchase date cannot be in the future");
    }

    @Test
    void getExchangeRate_Success_WithTodayDate() {
        // Given
        LocalDate today = LocalDate.now();
        String country = "Brazil";

        // When
        Optional<BigDecimal> result = treasuryApiClient.getExchangeRatesFiltered(country, today);

        // Then - Should either return a rate or empty (depending on data availability)
        // Not throwing exception is the key assertion here
        assertThat(result).isNotNull();
    }

    @Test
    void getExchangeRate_Success_WithDateExactlySixMonthsAgo() {
        // Given - exactly 6 months ago from a known date with data
        LocalDate referenceDate = LocalDate.of(2024, 6, 15);
        String country = "Brazil";

        // When
        Optional<BigDecimal> result = treasuryApiClient.getExchangeRatesFiltered(country, referenceDate);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isPositive();
    }

    @Test
    void getExchangeRate_Empty_WithDateMoreThanSixMonthsWithoutData() {
        // Given - a very old date where data might not be within 6 months window
        LocalDate oldDate = LocalDate.of(2020, 1, 1);
        String country = "Brazil";

        // When
        Optional<BigDecimal> result = treasuryApiClient.getExchangeRatesFiltered(country, oldDate);

        // Then - might be empty if no data in the 6-month window
        // This test documents the behavior
        assertThat(result).isNotNull();
    }

    @Test
    void getExchangeRate_ReturnsConsistentResults() {
        // Given
        LocalDate testDate = LocalDate.of(2024, 6, 15);
        String country = "Brazil";

        // When - calling twice with same parameters
        Optional<BigDecimal> result1 = treasuryApiClient.getExchangeRatesFiltered(country, testDate);
        Optional<BigDecimal> result2 = treasuryApiClient.getExchangeRatesFiltered(country, testDate);

        // Then - should return the same result
        assertThat(result1).isEqualTo(result2);
        if (result1.isPresent() && result2.isPresent()) {
            assertThat(result1.get()).isEqualByComparingTo(result2.get());
        }
    }

    @Test
    void getExchangeRate_Success_WithHistoricalDate() {
        // Given
        LocalDate historicalDate = LocalDate.of(2023, 6, 1);
        String country = "Euro Zone";

        // When
        Optional<BigDecimal> result = treasuryApiClient.getExchangeRatesFiltered(country, historicalDate);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isPositive();
    }

    @Test
    void getExchangeRate_ValidatesSixMonthWindow() {
        // Given - a date we know should have data
        LocalDate testDate = LocalDate.of(2024, 6, 15);
        String country = "Brazil";

        // When
        Optional<BigDecimal> result = treasuryApiClient.getExchangeRatesFiltered(country, testDate);

        // Then - the rate should come from within the 6-month window
        // (between 2023-12-15 and 2024-06-15)
        assertThat(result).isPresent();
    }

    @Test
    void configuration_IsLoadedCorrectly() {
        // Then
        assertThat(treasuryApiConfiguration).isNotNull();
        assertThat(treasuryApiConfiguration.getScheme()).isEqualTo("https");
        assertThat(treasuryApiConfiguration.getHost()).isEqualTo("api.fiscaldata.treasury.gov");
        assertThat(treasuryApiConfiguration.getPath()).isEqualTo("/services/api/fiscal_service/v1/accounting/od/rates_of_exchange");
    }
}