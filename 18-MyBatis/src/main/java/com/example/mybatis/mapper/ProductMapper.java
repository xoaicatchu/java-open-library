package com.example.mybatis.mapper;

import com.example.mybatis.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProductMapper {
    Product findById(Long id);
    List<Product> findWithCategory(RowBounds rowBounds);
    List<Product> findDynamic(@Param("name") String name, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, @Param("statusList") List<String> statusList);
    void insert(Product product);
    void update(Product product);
    void delete(Long id);
}
