package com.haoyangliu96.kafkademo_inventoryservice.services;

import com.haoyangliu96.kafkademo_inventoryservice.models.Product;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class InventoryRedisService {
    private static final String INVENTORY_KEY_PREFIX = "inventory";

    private final RedisTemplate<String, Product> redisTemplate;

    public InventoryRedisService(RedisTemplate<String, Product> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Product getInventory(UUID productId) {
        // get inventory from redis
        return redisTemplate.opsForValue().get(INVENTORY_KEY_PREFIX + productId);
    }

    public void setInventory(UUID productId, Product product) {
        // set inventory in redis
        redisTemplate.opsForValue().set(INVENTORY_KEY_PREFIX + productId, product, 1, TimeUnit.HOURS);
    }

    public void removeInventory(UUID productId) {
        // remove inventory from redis
        redisTemplate.delete(INVENTORY_KEY_PREFIX + productId);
    }
}
