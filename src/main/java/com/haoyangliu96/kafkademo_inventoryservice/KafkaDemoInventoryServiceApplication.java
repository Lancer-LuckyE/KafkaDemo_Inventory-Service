package com.haoyangliu96.kafkademo_inventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = "com.haoyangliu96.kafkademo.entities.inventoryservice")
@EnableJpaRepositories(basePackages = "com.haoyangliu96.kafkademo_inventoryservice.repositories")
public class KafkaDemoInventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaDemoInventoryServiceApplication.class, args);
    }

}
