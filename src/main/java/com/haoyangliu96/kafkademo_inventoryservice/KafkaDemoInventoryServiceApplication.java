package com.haoyangliu96.kafkademo_inventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class KafkaDemoInventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaDemoInventoryServiceApplication.class, args);
    }

}
