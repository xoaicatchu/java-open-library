package com.example.mapstruct.mapper;

import com.example.mapstruct.dto.OrderResponse;
import com.example.mapstruct.entity.Customer;
import com.example.mapstruct.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "order.orderNumber", target = "orderNum")
    @Mapping(source = "customer.email", target = "customerEmail")
    @Mapping(source = "customer.name", target = "customerName")
    OrderResponse toOrderResponse(Order order, Customer customer);
}
