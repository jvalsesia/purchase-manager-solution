package com.github.jvalsesia.pams;

import com.github.jvalsesia.pams.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({RsaKeyProperties.class})
public class PurchaseAuthorizationMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PurchaseAuthorizationMsApplication.class, args);
    }

}
