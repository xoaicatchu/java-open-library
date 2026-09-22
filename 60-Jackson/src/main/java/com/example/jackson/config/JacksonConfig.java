package com.example.jackson.config;

import com.example.jackson.domain.ThirdPartyUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper mapper = builder.build();
        
        // Add Mixin
        mapper.addMixIn(ThirdPartyUser.class, ThirdPartyUserMixin.class);
        
        // Add Filter Provider
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("dynamicFilter", SimpleBeanPropertyFilter.serializeAllExcept()); // default allow all
        mapper.setFilterProvider(filterProvider);
        
        return mapper;
    }
}
