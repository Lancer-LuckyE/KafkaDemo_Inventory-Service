package com.haoyangliu96.kafkademo_inventoryservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import com.haoyangliu96.kafkademo.entities.inventoryservice.Product;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
