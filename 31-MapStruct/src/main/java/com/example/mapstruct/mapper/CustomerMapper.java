package com.example.mapstruct.mapper;

import com.example.mapstruct.dto.CustomerInfo;
import com.example.mapstruct.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    
    CustomerInfo toDto(Customer customer);
    
    Customer toEntity(CustomerInfo dto);
    
    @Mapping(target = "id", ignore = true)
    void updateCustomerFromDto(CustomerInfo dto, @MappingTarget Customer customer);
}
