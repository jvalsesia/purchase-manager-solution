package com.github.jvalsesia.pcms.domain.service;

import com.github.jvalsesia.pcms.api.dto.ConvertedPurchaseResponse;
import com.github.jvalsesia.pcms.api.dto.PurchaseRequest;
import com.github.jvalsesia.pcms.api.dto.PurchaseResponse;
import com.github.jvalsesia.pcms.domain.exception.CurrencyConversionException;
import com.github.jvalsesia.pcms.domain.exception.PurchaseNotFoundException;
import com.github.jvalsesia.pcms.infrastructure.database.entity.Purchase;
import com.github.jvalsesia.pcms.infrastructure.database.repository.PurchaseRepository;
import com.github.jvalsesia.pcms.infrastructure.treasury.TreasuryApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final TreasuryApiClient treasuryApiClient;

    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request) {
        log.info("Creating purchase: {}", request.getDescription());

        Purchase purchase = new Purchase();
        purchase.setDescription(request.getDescription());
        purchase.setTransactionDate(request.getTransactionDate());
        purchase.setPurchaseAmount(request.getPurchaseAmount());

        Purchase saved = purchaseRepository.save(purchase);
        log.info("Purchase created with id: {}", saved.getId());

        return mapToPurchaseResponse(saved);
    }

    @Transactional(readOnly = true)
    public ConvertedPurchaseResponse getPurchaseInCurrency(UUID id, String country) {
        log.info("Converting purchase {} to currency for country: {}", id, country);

        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new PurchaseNotFoundException(id));

        BigDecimal exchangeRate = treasuryApiClient.getExchangeRatesFiltered(country, purchase.getTransactionDate())
                .orElseThrow(() -> new CurrencyConversionException(
                        String.format("The purchase cannot be converted to the target currency. " +
                                        "No exchange rate available for country '%s' within 6 months of transaction date %s",
                                country, purchase.getTransactionDate())
                ));

        BigDecimal convertedAmount = purchase.getPurchaseAmount()
                .multiply(exchangeRate)
                .setScale(2, RoundingMode.HALF_UP);

        log.info("Purchase {} converted: {} USD = {} (rate: {})",
                id, purchase.getPurchaseAmount(), convertedAmount, exchangeRate);

        return ConvertedPurchaseResponse.builder()
                .id(purchase.getId())
                .description(purchase.getDescription())
                .transactionDate(purchase.getTransactionDate())
                .originalAmount(purchase.getPurchaseAmount())
                .exchangeRate(exchangeRate)
                .convertedAmount(convertedAmount)
                .targetCurrency(country)
                .build();
    }

    private PurchaseResponse mapToPurchaseResponse(Purchase purchase) {
        return PurchaseResponse.builder()
                .id(purchase.getId())
                .description(purchase.getDescription())
                .transactionDate(purchase.getTransactionDate())
                .purchaseAmount(purchase.getPurchaseAmount())
                .build();
    }
}