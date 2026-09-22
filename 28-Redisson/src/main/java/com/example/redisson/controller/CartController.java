package com.example.redisson.controller;

import com.example.redisson.dto.CartItem;
import com.example.redisson.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<Void> addToCart(@PathVariable String userId, @RequestBody CartItem item) {
        cartService.addToCart(userId, item);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, CartItem>> getCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable String userId, @PathVariable String itemId) {
        cartService.removeFromCart(userId, itemId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lock-demo")
    public ResponseEntity<Boolean> lockDemo(@RequestParam String productId, @RequestParam int amount) {
        boolean success = cartService.updateInventoryWithLock(productId, amount);
        return ResponseEntity.ok(success);
    }
}
