package com.example.restclient.service;

import com.example.restclient.dto.ProductRecord;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;

@HttpExchange("/api/products")
public interface ProductHttpExchange {

    @GetExchange
    List<ProductRecord> getAll();

    @GetExchange("/{id}")
    ProductRecord getById(@PathVariable Long id);

    @PostExchange
    ProductRecord create(@RequestBody ProductRecord product);

    @PutExchange("/{id}")
    ProductRecord update(@PathVariable Long id, @RequestBody ProductRecord product);

    @DeleteExchange("/{id}")
    void delete(@PathVariable Long id);
}
