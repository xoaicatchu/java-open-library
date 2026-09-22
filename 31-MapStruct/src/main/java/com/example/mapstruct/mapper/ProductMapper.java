package com.example.mapstruct.mapper;

import com.example.mapstruct.dto.ProductSummary;
import com.example.mapstruct.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category", target = "category")
    @Mapping(source = "status", target = "statusString", qualifiedByName = "statusToString")
    @Mapping(target = "formattedPrice", expression = "java(\"$\" + product.getPrice())")
    ProductSummary toDto(Product product);

    List<ProductSummary> toDtoList(List<Product> products);

    @Named("statusToString")
    default String statusToString(Integer status) {
        if (status == null) return "UNKNOWN";
        return status == 1 ? "ACTIVE" : "INACTIVE";
    }
}
