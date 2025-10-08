package com.github.jvalsesia.pcms;

import com.github.jvalsesia.pcms.infrastructure.security.config.IssuerUriConfig;
import com.github.jvalsesia.pcms.infrastructure.treasury.config.TreasuryApiConfiguration;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestClient;


@OpenAPIDefinition(info = @Info(
        title = "Purchase Transaction Converter Microservice REST API Documentation",
        description = "REST API Documentation",
        version = "v1",
        contact = @Contact(name = "Julio Valsesia",
                email = "jvalsesia@gmail.com",
                url = "https://github.com/jvalsesia"),
        license = @License(name = "MIT", url = "https://mit-license.org/")),
        externalDocs = @ExternalDocumentation(
                description = "Treasury Reporting Rates of Exchange API",
                url = "https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange")
)
@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(value = {TreasuryApiConfiguration.class, IssuerUriConfig.class})
public class PurchaseConverterMsApplication {

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

    public static void main(String[] args) {
        SpringApplication.run(PurchaseConverterMsApplication.class, args);
    }


}
