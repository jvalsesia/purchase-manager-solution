package com.github.jvalsesia.pcms.infrastructure.treasury;

import com.github.jvalsesia.pcms.infrastructure.treasury.config.TreasuryApiConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
public class TreasuryApiClient {

    TreasuryApiConfiguration treasuryApiConfiguration;

    private final RestClient restClient;

    public TreasuryApiClient(RestClient restClient, TreasuryApiConfiguration treasuryApiConfiguration) {
        this.restClient = restClient;
        this.treasuryApiConfiguration = treasuryApiConfiguration;
    }


    public Optional<BigDecimal> getExchangeRatesFiltered(String country, LocalDate purchaseDate) {
        // log url
        log.info("{}://{}{}", treasuryApiConfiguration.getScheme(),
                treasuryApiConfiguration.getHost(),
                treasuryApiConfiguration.getHost());

        // check future date
        if (purchaseDate.isAfter(LocalDate.now())) {
            throw new TreasuryApiException("Purchase date cannot be in the future");
        }

        // check 6 months
       LocalDate sixMonthsAgo = purchaseDate.minusMonths(6);

        try {
            TreasuryApiResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme(treasuryApiConfiguration.getScheme())
                            .host(treasuryApiConfiguration.getHost())
                            .path(treasuryApiConfiguration.getPath())
                            .queryParam("fields", "effective_date,country,currency,exchange_rate")
                            .queryParam("sort", "-effective_date")
                            .queryParam("filter", String.format(
                                    "country:eq:%s,effective_date:lte:%s,effective_date:gte:%s", country, purchaseDate, sixMonthsAgo))
                            .build())
                    .retrieve()
                    .body(TreasuryApiResponse.class);

            if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                BigDecimal rate = response.getData().getFirst().getExchangeRate();
                log.info("Found exchange rate: {} for country: {}", rate, country);
                log.info("Exchange rate data: {}", response.getData());
                return Optional.of(rate);
            }

            log.warn("No exchange rate found for country: {} within 6 months of {}", country, purchaseDate);
            return Optional.empty();


        } catch (Exception e) {
            log.error("Error fetching exchange rate from Treasury API", e);
            throw new TreasuryApiException("Failed to fetch exchange rate: " + e.getMessage(), e);
        }
    }

}