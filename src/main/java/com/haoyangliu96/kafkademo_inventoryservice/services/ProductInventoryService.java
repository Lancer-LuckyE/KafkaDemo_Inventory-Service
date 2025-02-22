package com.haoyangliu96.kafkademo_inventoryservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haoyangliu96.kafkademo.dtos.order.OrderItemsDTO;
import com.haoyangliu96.kafkademo.events.AbstractOrderEvent;
import com.haoyangliu96.kafkademo.events.OrderCreatedEvent;
import com.haoyangliu96.kafkademo.exceptions.KafkaEventException;
import com.haoyangliu96.kafkademo.exceptions.NotFoundException;
import com.haoyangliu96.kafkademo_inventoryservice.models.Product;
import com.haoyangliu96.kafkademo_inventoryservice.repositories.ProductRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class ProductInventoryService {
    private final Logger logger = Logger.getLogger(ProductInventoryService.class.getName());

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final InventoryRedisService inventoryRedisService;

    public ProductInventoryService(ProductRepository productRepository, KafkaTemplate<String, String> kafkaTemplate, InventoryRedisService inventoryRedisService) {
        this.productRepository = productRepository;
        this.objectMapper = new ObjectMapper();
        this.kafkaTemplate = kafkaTemplate;
        this.inventoryRedisService = inventoryRedisService;
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
        Product product = inventoryRedisService.getInventory(productId);
        if (product == null) {
            product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found"));
            inventoryRedisService.setInventory(productId, product);
        } else {
            logger.info("Retrieved product from cache: " + product);
        }
        return product;
    }

    public List<Product> getAllProducts() {
        // get all products
        List<Product> allProducts = productRepository.findAll();
        allProducts.forEach(product -> inventoryRedisService.setInventory(product.getId(), product));
        return allProducts;
    }

    @KafkaListener(topics = "orders", groupId = "inventory-group")
    public void handleOrderEvent(ConsumerRecord<String, String> eventRecord, Acknowledgment ack) {
        String eventJson = eventRecord.value();
        try {
            AbstractOrderEvent event = objectMapper.readValue(eventJson, AbstractOrderEvent.class);
            switch (event.getEventType()) {
                case ORDER_CREATED -> {
                    if (event instanceof OrderCreatedEvent orderCreatedEvent) {
                        handleOrderCreatedEvent(orderCreatedEvent);
                    }
                }
                default -> throw new KafkaEventException("Unknown event type: " + event.getEventType());
            }
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            String message = String.format("Failed to parse event [%s]: %s", eventJson,e.getMessage());
            throw new KafkaEventException(message);
        }
    }

    private void handleOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {
        List<OrderItemsDTO> orderItems = orderCreatedEvent.getItemIds();
        logger.info("Received order created event for order: " + orderCreatedEvent.getOrderId() + " with items: " + orderItems);
        // check stock
        if (checkStockForEachItem(orderItems)) {
            // send order waiting for payment event
            kafkaTemplate.send("inventory", orderCreatedEvent.getOrderId().toString(), "INVENTORY_CONFIRMED_EVENT"); //TODO: create inventory confirmed event
        } else {
            // send out of stock event
            kafkaTemplate.send("inventory", orderCreatedEvent.getOrderId().toString(), "OUT_OF_STOCK_EVENT"); //TODO: create out of stock event
        }
    }

    private boolean checkStockForEachItem(List<OrderItemsDTO> orderItemsDto) {
        return orderItemsDto.stream()
                .map(orderItem -> { // check stock for each item and map to boolean stream
                    Product product = getProductById(orderItem.getItem().getItemId());
                    return product.getStock() >= orderItem.getQuantity();
                })
                .reduce(true, Boolean::logicalAnd); // reduce to a single boolean value, any false will return false
    }
}
