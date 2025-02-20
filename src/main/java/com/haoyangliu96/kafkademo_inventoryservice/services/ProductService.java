package com.haoyangliu96.kafkademo_inventoryservice.services;

import com.haoyangliu96.kafkademo_inventoryservice.dto.ProductDTO;
import com.haoyangliu96.kafkademo_inventoryservice.exceptions.NotFoundException;
import com.haoyangliu96.kafkademo_inventoryservice.models.Product;
import com.haoyangliu96.kafkademo_inventoryservice.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class ProductService {
    private final Logger logger = Logger.getLogger(ProductService.class.getName());

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product saveProduct(Product product) {
        // save product
        logger.info("Saving product: " + product);
        return productRepository.save(product);
    }

    public void saveAllProducts(List<Product> products) {
        // save all products
        productRepository.saveAll(products);
    }

    public Product getProductById(UUID productId) {
        // get product by id
        return productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found"));
    }

    public List<Product> getAllProducts() {
        // get all products
        return productRepository.findAll();
    }
}
