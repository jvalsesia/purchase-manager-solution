package com.github.jvalsesia.pcms.domain.service;

import com.github.jvalsesia.pcms.api.dto.ConvertedPurchaseResponse;
import com.github.jvalsesia.pcms.api.dto.PurchaseRequest;
import com.github.jvalsesia.pcms.api.dto.PurchaseResponse;
import com.github.jvalsesia.pcms.domain.exception.CurrencyConversionException;
import com.github.jvalsesia.pcms.domain.exception.PurchaseNotFoundException;
import com.github.jvalsesia.pcms.infrastructure.database.entity.Purchase;
import com.github.jvalsesia.pcms.infrastructure.database.repository.PurchaseRepository;
import com.github.jvalsesia.pcms.infrastructure.treasury.TreasuryApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private TreasuryApiClient treasuryApiClient;

    @InjectMocks
    private PurchaseService purchaseService;

    private PurchaseRequest validRequest;
    private Purchase savedPurchase;

    @BeforeEach
    void setUp() {
        validRequest = new PurchaseRequest();
        validRequest.setDescription("Test Purchase");
        validRequest.setTransactionDate(LocalDate.of(2024, 1, 15));
        validRequest.setPurchaseAmount(new BigDecimal("100.00"));

        savedPurchase = new Purchase();
        savedPurchase.setId(UUID.randomUUID());
        savedPurchase.setDescription("Test Purchase");
        savedPurchase.setTransactionDate(LocalDate.of(2024, 1, 15));
        savedPurchase.setPurchaseAmount(new BigDecimal("100.00"));
    }

    @Test
    void createPurchase_Success() {
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(savedPurchase);

        PurchaseResponse response = purchaseService.createPurchase(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(savedPurchase.getId());
        assertThat(response.getDescription()).isEqualTo("Test Purchase");
        assertThat(response.getPurchaseAmount()).isEqualByComparingTo("100.00");

        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }

    @Test
    void getPurchaseInCurrency_Success() {
        UUID purchaseId = savedPurchase.getId();
        BigDecimal exchangeRate = new BigDecimal("5.5");

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(savedPurchase));
        when(treasuryApiClient.getExchangeRatesFiltered("Brazil", savedPurchase.getTransactionDate()))
                .thenReturn(Optional.of(exchangeRate));

        ConvertedPurchaseResponse response = purchaseService.getPurchaseInCurrency(purchaseId, "Brazil");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(purchaseId);
        assertThat(response.getOriginalAmount()).isEqualByComparingTo("100.00");
        assertThat(response.getExchangeRate()).isEqualByComparingTo("5.5");
        assertThat(response.getConvertedAmount()).isEqualByComparingTo("550.00");
        assertThat(response.getTargetCurrency()).isEqualTo("Brazil");

        verify(purchaseRepository, times(1)).findById(purchaseId);
        verify(treasuryApiClient, times(1)).getExchangeRatesFiltered("Brazil", savedPurchase.getTransactionDate());
    }

    @Test
    void getPurchaseInCurrency_PurchaseNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        when(purchaseRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.getPurchaseInCurrency(nonExistentId, "Brazil"))
                .isInstanceOf(PurchaseNotFoundException.class)
                .hasMessageContaining(nonExistentId.toString());

        verify(treasuryApiClient, never()).getExchangeRatesFiltered(anyString(), any(LocalDate.class));
    }

    @Test
    void getPurchaseInCurrency_NoExchangeRateAvailable() {
        UUID purchaseId = savedPurchase.getId();

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(savedPurchase));
        when(treasuryApiClient.getExchangeRatesFiltered("Brazil", savedPurchase.getTransactionDate()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.getPurchaseInCurrency(purchaseId, "Brazil"))
                .isInstanceOf(CurrencyConversionException.class)
                .hasMessageContaining("cannot be converted");
    }

    @Test
    void getPurchaseInCurrency_RoundingToTwoDecimalPlaces() {
        UUID purchaseId = savedPurchase.getId();
        BigDecimal exchangeRate = new BigDecimal("1.3333");

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(savedPurchase));
        when(treasuryApiClient.getExchangeRatesFiltered("Canada", savedPurchase.getTransactionDate()))
                .thenReturn(Optional.of(exchangeRate));

        ConvertedPurchaseResponse response = purchaseService.getPurchaseInCurrency(purchaseId, "Canada");

        assertThat(response.getConvertedAmount()).isEqualByComparingTo("133.33");
    }
}