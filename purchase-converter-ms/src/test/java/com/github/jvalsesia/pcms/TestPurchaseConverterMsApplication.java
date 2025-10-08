package com.github.jvalsesia.pcms;

import org.springframework.boot.SpringApplication;

public class TestPurchaseConverterMsApplication {

    public static void main(String[] args) {
        SpringApplication.from(PurchaseConverterMsApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
