package com.example.mybatis.service;

import com.example.mybatis.dto.CreateProductRequest;
import com.example.mybatis.dto.ProductResponse;
import com.example.mybatis.dto.UpdateProductRequest;
import com.example.mybatis.entity.Product;
import com.example.mybatis.mapper.ProductMapper;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductMapper productMapper;

    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public List<ProductResponse> findAll(int offset, int limit) {
        RowBounds rowBounds = new RowBounds(offset, limit);
        return productMapper.findWithCategory(rowBounds).stream()
            .map(ProductResponse::from)
            .toList();
    }

    public ProductResponse findById(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm với id: " + id);
        }
        return ProductResponse.from(product);
    }

    public List<ProductResponse> search(String name, BigDecimal minPrice, BigDecimal maxPrice, List<String> statusList) {
        return productMapper.findDynamic(name, minPrice, maxPrice, statusList).stream()
            .map(ProductResponse::from)
            .toList();
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());
        product.setCategoryId(request.categoryId());
        product.setStatus(request.status());
        productMapper.insert(product);
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm với id: " + id);
        }
        product.setName(request.name());
        product.setPrice(request.price());
        product.setCategoryId(request.categoryId());
        product.setStatus(request.status());
        productMapper.update(product);
        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        productMapper.delete(id);
    }
}
