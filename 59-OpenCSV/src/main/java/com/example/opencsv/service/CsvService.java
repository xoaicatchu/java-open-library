package com.example.opencsv.service;

import com.example.opencsv.entity.Product;
import com.example.opencsv.repository.ProductRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CsvService {

    private final ProductRepository productRepository;

    public CsvService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Ghi file CSV
    public void exportProducts(Writer writer) throws Exception {
        HeaderColumnNameMappingStrategy<Product> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(Product.class);

        StatefulBeanToCsv<Product> beanToCsv = new StatefulBeanToCsvBuilder<Product>(writer)
                .withMappingStrategy(strategy)
                .withSeparator(',')
                .withQuotechar('"')
                .build();

        List<Product> products = productRepository.findAll();
        beanToCsv.write(products);
    }

    // Doc file CSV
    @Transactional
    public List<Product> importProducts(MultipartFile file) throws Exception {
        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            HeaderColumnNameMappingStrategy<Product> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(Product.class);

            CsvToBean<Product> csvToBean = new CsvToBeanBuilder<Product>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<Product> products = csvToBean.parse();
            return productRepository.saveAll(products);
        }
    }
}
