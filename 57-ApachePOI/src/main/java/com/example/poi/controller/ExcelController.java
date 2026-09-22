package com.example.poi.controller;

import com.example.poi.dto.ProductDto;
import com.example.poi.service.ExcelService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    private final ExcelService excelService;

    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportProducts() throws IOException {
        byte[] fileContent = excelService.exportProducts();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "products.xlsx");

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    @PostMapping("/import")
    public ResponseEntity<List<ProductDto>> importProducts(@RequestParam("file") MultipartFile file) throws IOException {
        List<ProductDto> result = excelService.importProducts(file);
        return ResponseEntity.ok(result);
    }
}
