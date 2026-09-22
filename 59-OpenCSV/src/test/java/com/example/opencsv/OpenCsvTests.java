package com.example.opencsv;

import com.example.opencsv.entity.Product;
import com.example.opencsv.repository.ProductRepository;
import com.opencsv.*;
import com.opencsv.bean.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OpenCsvTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void test1CsvWriter() throws Exception {
        // Ghi CSV cơ bản
        StringWriter sw = new StringWriter();
        try (CSVWriter writer = new CSVWriter(sw)) {
            writer.writeNext(new String[]{"ID", "Name", "Price"});
            writer.writeNext(new String[]{"1", "Test", "10.0"});
        }
        assertThat(sw.toString()).contains("Test");
    }

    @Test
    void test2CsvReader() throws Exception {
        // Đọc CSV cơ bản
        String csv = "ID,Name,Price\n1,Test,10.0";
        StringReader sr = new StringReader(csv);
        try (CSVReader reader = new CSVReaderBuilder(sr).build()) {
            List<String[]> lines = reader.readAll();
            assertThat(lines).hasSize(2);
            assertThat(lines.get(1)[1]).isEqualTo("Test");
        }
    }

    @Test
    void test3BeanMappingCustomSeparator() throws Exception {
        // Phân cách bằng tab
        String csv = "Name\tPrice\nApple\t1.5";
        StringReader sr = new StringReader(csv);

        CSVParser parser = new CSVParserBuilder().withSeparator('\t').build();
        CSVReader reader = new CSVReaderBuilder(sr).withCSVParser(parser).build();

        HeaderColumnNameMappingStrategy<Product> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(Product.class);

        CsvToBean<Product> csvToBean = new CsvToBeanBuilder<Product>(reader)
                .withMappingStrategy(strategy)
                .build();

        List<Product> products = csvToBean.parse();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Apple");
        assertThat(products.get(0).getPrice()).isEqualTo(1.5);
    }

    @Test
    void test4RestImportExport() throws Exception {
        productRepository.deleteAll();

        // Import CSV qua REST
        String csvData = "Name,Price\nBanana,2.0";
        MockMultipartFile file = new MockMultipartFile(
                "file", "products.csv", "text/csv", csvData.getBytes());

        mockMvc.perform(multipart("/api/csv/import")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Banana"));

        assertThat(productRepository.findAll()).hasSize(1);

        // Export CSV qua REST
        mockMvc.perform(get("/api/csv/export"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Banana")));
    }

    @Test
    void test5CsvConstraintViolation() {
        // Lỗi validation khi thiếu trường bắt buộc
        String csv = "Price\n2.0";
        StringReader sr = new StringReader(csv);
        
        HeaderColumnNameMappingStrategy<Product> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(Product.class);

        CsvToBean<Product> csvToBean = new CsvToBeanBuilder<Product>(sr)
                .withMappingStrategy(strategy)
                .withThrowExceptions(true)
                .build();

        try {
            csvToBean.parse();
        } catch (RuntimeException e) {
            assertThat(e.getCause()).isInstanceOf(com.opencsv.exceptions.CsvRequiredFieldEmptyException.class);
        }
    }

    @Test
    void test6BindByPosition() throws Exception {
        // Mapping theo vị trí (không cần header)
        String csv = "Orange,3.5";
        StringReader sr = new StringReader(csv);
        
        ColumnPositionMappingStrategy<Product> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(Product.class);
        
        CsvToBean<Product> csvToBean = new CsvToBeanBuilder<Product>(sr)
                .withMappingStrategy(strategy)
                .build();
                
        List<Product> products = csvToBean.parse();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Orange");
        assertThat(products.get(0).getPrice()).isEqualTo(3.5);
    }
}
