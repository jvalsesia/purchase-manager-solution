package com.github.jvalsesia.pcms.api.controller;

import com.github.jvalsesia.pcms.api.dto.ConvertedPurchaseResponse;
import com.github.jvalsesia.pcms.api.dto.PurchaseRequest;
import com.github.jvalsesia.pcms.api.dto.PurchaseResponse;
import com.github.jvalsesia.pcms.api.exception.ErrorResponse;
import com.github.jvalsesia.pcms.domain.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@EnableMethodSecurity
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Operation(summary = "Create Purchase REST API", description = "REST API to create new Purchase")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "400", description = "HTTP Status BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/purchases")
    @PreAuthorize("hasAnyAuthority('SCOPE_purchase')")
    public ResponseEntity<PurchaseResponse> createPurchase(@Valid @RequestBody PurchaseRequest request) {
        PurchaseResponse response = purchaseService.createPurchase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Fetch Purchase REST API", description = "REST API to Purchase based on id")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "400", description = "HTTP Status BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/purchases/{id}/convert")
    @PreAuthorize("hasAnyAuthority('SCOPE_purchase')")
    public ResponseEntity<ConvertedPurchaseResponse> getPurchaseInCurrency(
            @PathVariable UUID id,
            @RequestParam String country) {
        ConvertedPurchaseResponse response = purchaseService.getPurchaseInCurrency(id, country);
        return ResponseEntity.ok(response);
    }
}