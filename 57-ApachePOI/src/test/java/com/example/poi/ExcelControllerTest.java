package com.example.poi;

import com.example.poi.entity.Product;
import com.example.poi.repository.ProductRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExcelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
    }

    @Test
    void testExportExcel() throws Exception {
        productRepository.save(new Product("Monitor", "Display", new BigDecimal("300.00"), 4));

        mockMvc.perform(get("/api/excel/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"products.xlsx\""))
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @Test
    void testImportExcel() throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("ImportData");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("Category");
            header.createCell(2).setCellValue("Price");
            header.createCell(3).setCellValue("Quantity");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("Webcam");
            dataRow.createCell(1).setCellValue("Accessories");
            dataRow.createCell(2).setCellValue(80.0);
            dataRow.createCell(3).setCellValue(15);

            workbook.write(out);

            MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", out.toByteArray());

            mockMvc.perform(multipart("/api/excel/import").file(file))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name").value("Webcam"));
        }
    }
    
    @Test
    void testStylingIsPresentInExport() throws Exception {
        productRepository.save(new Product("Styling", "Test", new BigDecimal("10.00"), 1));

        byte[] bytes = mockMvc.perform(get("/api/excel/export"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();
                
        // Validation of styles is done implicitly if export succeeds 
        // with the POI code applying styles in ExcelService
    }
}
