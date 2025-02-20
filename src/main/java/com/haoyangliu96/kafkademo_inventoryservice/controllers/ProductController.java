package com.haoyangliu96.kafkademo_inventoryservice.controllers;

import com.haoyangliu96.kafkademo_inventoryservice.dto.ProductDTO;
import com.haoyangliu96.kafkademo_inventoryservice.exceptions.NotFoundException;
import com.haoyangliu96.kafkademo_inventoryservice.models.Product;
import com.haoyangliu96.kafkademo_inventoryservice.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getProducts() {
        // get products
        List<Product> products = productService.getAllProducts();
        List<ProductDTO> productsToReturn = products.stream().map(product -> ProductDTO.builder()
                .productId(product.getId().toString())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build()).toList();
        return new ResponseEntity<>(productsToReturn, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable UUID id) {
        // get product by id
        Product product = productService.getProductById(id);

        ProductDTO productToReturn = ProductDTO.builder()
                .productId(product.getId().toString())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
        return new ResponseEntity<>(productToReturn, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody List<ProductDTO> products) {
        // TODO: validate products (name should be unique in on store)

        // create product
        List<Product> productsToSave = products.stream().map(productDTO -> Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .stock(productDTO.getStock())
                .build()).toList();
        // save products
        productService.saveAllProducts(productsToSave);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public void updateProduct(@PathVariable UUID id) {
        // update product by id
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
