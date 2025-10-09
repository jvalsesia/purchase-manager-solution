package com.github.jvalsesia.pcms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PurchaseConverterMsApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethodStartsApplication() {
        PurchaseConverterMsApplication.main(new String[] {});
    }
}
