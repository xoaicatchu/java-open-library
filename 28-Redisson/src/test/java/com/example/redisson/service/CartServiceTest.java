package com.example.redisson.service;

import com.example.redisson.dto.CartItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.*;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RMap<String, CartItem> rMap;

    @Mock
    private RLock rLock;

    @Mock
    private RAtomicLong rAtomicLong;

    @Mock
    private RQueue<String> rQueue;

    @Mock
    private RTopic rTopic;

    @Mock
    private RBucket<String> rBucket;

    @InjectMocks
    private CartService cartService;

    @Test
    void testAddToCart() {
        when(redissonClient.<String, CartItem>getMap("cart:session123")).thenReturn(rMap);
        CartItem item = new CartItem("p1", 2, 10.5);

        cartService.addToCart("session123", item);

        verify(rMap, times(1)).put("p1", item);
    }

    @Test
    void testUpdateInventoryWithLockSuccess() throws InterruptedException {
        when(redissonClient.getLock("inventoryLock:p1")).thenReturn(rLock);
        when(rLock.tryLock(5, 10, TimeUnit.SECONDS)).thenReturn(true);

        boolean result = cartService.updateInventoryWithLock("p1", 10);

        assertTrue(result);
        verify(rLock, times(1)).unlock();
    }

    @Test
    void testIncrementVisit() {
        when(redissonClient.getAtomicLong("siteVisits")).thenReturn(rAtomicLong);
        when(rAtomicLong.incrementAndGet()).thenReturn(42L);

        long result = cartService.incrementVisit();

        assertEquals(42L, result);
    }

    @Test
    void testEnqueueOrder() {
        when(redissonClient.<String>getQueue("orderQueue")).thenReturn(rQueue);

        cartService.enqueueOrder("order999");

        verify(rQueue, times(1)).add("order999");
    }

    @Test
    void testPublishPriceChange() {
        when(redissonClient.getTopic("priceUpdates")).thenReturn(rTopic);

        cartService.publishPriceChange("p1", 19.99);

        verify(rTopic, times(1)).publish("Product p1 price changed to 19.99");
    }
    
    @Test
    void testSaveSessionData() {
        when(redissonClient.<String>getBucket("sessionData:session123")).thenReturn(rBucket);
        
        cartService.saveSessionData("session123", "data");
        
        verify(rBucket, times(1)).set("data", 30, TimeUnit.MINUTES);
    }
}
