package com.example.opencsv.controller;

import com.example.opencsv.entity.Product;
import com.example.opencsv.service.CsvService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/csv")
public class CsvController {

    private final CsvService csvService;

    public CsvController(CsvService csvService) {
        this.csvService = csvService;
    }

    @GetMapping("/export")
    public void exportProducts(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"products.csv\"");
        csvService.exportProducts(response.getWriter());
    }

    @PostMapping("/import")
    public ResponseEntity<List<Product>> importProducts(@RequestParam("file") MultipartFile file) throws Exception {
        List<Product> products = csvService.importProducts(file);
        return ResponseEntity.ok(products);
    }
}
