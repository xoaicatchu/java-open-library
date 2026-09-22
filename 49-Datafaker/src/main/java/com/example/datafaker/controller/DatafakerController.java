package com.example.datafaker.controller;

import com.example.datafaker.dto.CustomerDto;
import com.example.datafaker.dto.ProductDto;
import com.example.datafaker.entity.Customer;
import com.example.datafaker.entity.Product;
import com.example.datafaker.service.FakerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/faker")
public class DatafakerController {

    private final FakerService fakerService;

    public DatafakerController(FakerService fakerService) {
        this.fakerService = fakerService;
    }

    @PostMapping("/seed")
    public String seed() {
        fakerService.seedDatabase();
        return "Database seeded successfully!";
    }

    @GetMapping("/customers/vn")
    public List<CustomerDto> getVnCustomers() {
        return fakerService.generateVietnameseCustomers(5).stream()
                .map(c -> new CustomerDto(c.getFullName(), c.getEmail(), c.getPhoneNumber(), c.getAddress()))
                .collect(Collectors.toList());
    }
    
    @GetMapping("/products/expression")
    public List<ProductDto> getProductsExpression() {
        return fakerService.generateProductsFromExpression(3).stream()
                .map(p -> new ProductDto(p.getName(), p.getDepartment(), p.getPrice()))
                .collect(Collectors.toList());
    }
}
