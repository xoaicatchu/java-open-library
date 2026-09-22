package com.example.redisson.service;

import com.example.redisson.dto.CartItem;
import org.redisson.api.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CartService {

    private final RedissonClient redissonClient;

    public CartService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    // 1. RMap - distributed map for shopping cart
    public void addToCart(String sessionId, CartItem item) {
        RMap<String, CartItem> cart = redissonClient.getMap("cart:" + sessionId);
        cart.put(item.productId(), item);
    }

    public Map<String, CartItem> getCart(String sessionId) {
        return redissonClient.getMap("cart:" + sessionId);
    }

    public void removeFromCart(String sessionId, String productId) {
        RMap<String, CartItem> cart = redissonClient.getMap("cart:" + sessionId);
        cart.remove(productId);
    }

    // 2. RLock - distributed lock for inventory check
    public boolean updateInventoryWithLock(String productId, int amount) {
        RLock lock = redissonClient.getLock("inventoryLock:" + productId);
        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                try {
                    // Cập nhật inventory
                    return true;
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    // 3. RAtomicLong - distributed counter for visits
    public long incrementVisit() {
        RAtomicLong visitCounter = redissonClient.getAtomicLong("siteVisits");
        return visitCounter.incrementAndGet();
    }

    // 4. RQueue - distributed queue for orders
    public void enqueueOrder(String orderId) {
        RQueue<String> orderQueue = redissonClient.getQueue("orderQueue");
        orderQueue.add(orderId);
    }

    // 5. RTopic - pub/sub for price changes
    public void publishPriceChange(String productId, double newPrice) {
        RTopic topic = redissonClient.getTopic("priceUpdates");
        topic.publish("Product " + productId + " price changed to " + newPrice);
    }

    // 6. RBucket - simple key-value for session data
    public void saveSessionData(String sessionId, String data) {
        RBucket<String> bucket = redissonClient.getBucket("sessionData:" + sessionId);
        bucket.set(data, 30, TimeUnit.MINUTES);
    }

    // 7. Spring Cache
    @Cacheable(value = "productCache", key = "#productId")
    public String getProductInfo(String productId) {
        // Simulate DB fetch
        return "Product Info for " + productId;
    }
}
