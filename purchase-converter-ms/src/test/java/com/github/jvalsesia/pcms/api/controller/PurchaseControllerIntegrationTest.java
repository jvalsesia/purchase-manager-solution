package com.github.jvalsesia.pcms.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jvalsesia.pcms.api.dto.PurchaseRequest;
import com.github.jvalsesia.pcms.infrastructure.database.entity.Purchase;
import com.github.jvalsesia.pcms.infrastructure.database.repository.PurchaseRepository;
import com.github.jvalsesia.pcms.infrastructure.treasury.TreasuryApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PurchaseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @MockitoBean
    private TreasuryApiClient treasuryApiClient;

    @BeforeEach
    void setUp() {
        purchaseRepository.deleteAll();
    }

    @Test
    void createPurchase_ValidRequest_ReturnsCreated() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Laptop");
        request.setTransactionDate(LocalDate.of(2024, 1, 15));
        request.setPurchaseAmount(new BigDecimal("1500.00"));

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_purchase"))))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Laptop"))
                .andExpect(jsonPath("$.transactionDate").value("2024-01-15"))
                .andExpect(jsonPath("$.purchaseAmount").value(1500.00));
    }

    @Test
    void createPurchase_InvalidDescription_ReturnsBadRequest() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("A".repeat(51)); // Exceeds 50 characters
        request.setTransactionDate(LocalDate.of(2024, 1, 15));
        request.setPurchaseAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_purchase"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    void createPurchase_NegativeAmount_ReturnsBadRequest() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Invalid Purchase");
        request.setTransactionDate(LocalDate.of(2024, 1, 15));
        request.setPurchaseAmount(new BigDecimal("-10.00"));

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_purchase"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPurchaseInCurrency_ValidRequest_ReturnsConverted() throws Exception {
        Purchase purchase = new Purchase();
        purchase.setDescription("Test Purchase");
        purchase.setTransactionDate(LocalDate.of(2024, 1, 15));
        purchase.setPurchaseAmount(new BigDecimal("100.00"));
        Purchase saved = purchaseRepository.save(purchase);

        when(treasuryApiClient.getExchangeRatesFiltered(eq("Brazil"), any(LocalDate.class)))
                .thenReturn(Optional.of(new BigDecimal("5.0")));

        mockMvc.perform(get("/api/purchases/{id}/convert", saved.getId())
                        .param("country", "Brazil")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_purchase"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.originalAmount").value(100.00))
                .andExpect(jsonPath("$.exchangeRate").value(5.0))
                .andExpect(jsonPath("$.convertedAmount").value(500.00))
                .andExpect(jsonPath("$.targetCurrency").value("Brazil"));
    }

    @Test
    void getPurchaseInCurrency_NonExistentId_ReturnsNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/api/purchases/{id}/convert", randomId)
                        .param("country", "Brazil")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_purchase"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void getPurchaseInCurrency_NoExchangeRate_ReturnsBadRequest() throws Exception {
        Purchase purchase = new Purchase();
        purchase.setDescription("Test Purchase");
        purchase.setTransactionDate(LocalDate.of(2024, 1, 15));
        purchase.setPurchaseAmount(new BigDecimal("100.00"));
        Purchase saved = purchaseRepository.save(purchase);

        when(treasuryApiClient.getExchangeRatesFiltered(eq("Brazil"), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/purchases/{id}/convert", saved.getId())
                        .param("country", "Brazil")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_purchase"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Currency Conversion Error"));
    }
}