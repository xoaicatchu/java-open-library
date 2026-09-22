package com.example.poi;

import com.example.poi.dto.ProductDto;
import com.example.poi.entity.Product;
import com.example.poi.repository.ProductRepository;
import com.example.poi.service.ExcelService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ExcelServiceTest {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
    }

    @Test
    void testCreateWorkbook() throws IOException {
        productRepository.save(new Product("Laptop", "Electronics", new BigDecimal("1000.00"), 2));

        byte[] bytes = excelService.exportProducts();
        assertThat(bytes).isNotEmpty();

        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            assertThat(sheet.getSheetName()).isEqualTo("Products");

            Row headerRow = sheet.getRow(0);
            assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("ID");

            Row firstDataRow = sheet.getRow(1);
            assertThat(firstDataRow.getCell(1).getStringCellValue()).isEqualTo("Laptop");
        }
    }

    @Test
    void testFormulasInWorkbook() throws IOException {
        productRepository.save(new Product("Mouse", "Accessories", new BigDecimal("50.00"), 5));

        byte[] bytes = excelService.exportProducts();
        
        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row firstDataRow = sheet.getRow(1);
            
            // Check formula: D2*E2
            assertThat(firstDataRow.getCell(5).getCellFormula()).isEqualTo("D2*E2");
        }
    }

    @Test
    void testReadWorkbookAndImport() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("ImportData");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("Category");
            header.createCell(2).setCellValue("Price");
            header.createCell(3).setCellValue("Quantity");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("Keyboard");
            dataRow.createCell(1).setCellValue("Accessories");
            dataRow.createCell(2).setCellValue(100.0);
            dataRow.createCell(3).setCellValue(10);

            workbook.write(out);
            byte[] fileBytes = out.toByteArray();

            MockMultipartFile file = new MockMultipartFile("file", "import.xlsx", 
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", fileBytes);
            
            List<ProductDto> result = excelService.importProducts(file);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("Keyboard");
            assertThat(result.get(0).price()).isEqualByComparingTo(new BigDecimal("100.0"));
            
            assertThat(productRepository.count()).isEqualTo(1);
        }
    }
}
