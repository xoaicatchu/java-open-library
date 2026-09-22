package com.example.jdbi.config;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@Configuration
public class JdbiConfig {

    @Bean
    public Jdbi jdbi(DataSource dataSource) {
        // Cấu hình JDBI sử dụng DataSource của Spring
        TransactionAwareDataSourceProxy proxy = new TransactionAwareDataSourceProxy(dataSource);
        Jdbi jdbi = Jdbi.create(proxy);
        
        // Đăng ký Plugin cho SqlObject API
        jdbi.installPlugin(new SqlObjectPlugin());
        
        // Đăng ký toàn cục cho ConstructorMapper và ColumnMapper
        jdbi.registerRowMapper(org.jdbi.v3.core.mapper.reflect.ConstructorMapper.factory(com.example.jdbi.domain.Employee.class));
        jdbi.registerColumnMapper(new EmailColumnMapper());
        
        return jdbi;
    }
}
