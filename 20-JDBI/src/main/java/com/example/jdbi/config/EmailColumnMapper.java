package com.example.jdbi.config;

import com.example.jdbi.domain.Email;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmailColumnMapper implements ColumnMapper<Email> {
    @Override
    public Email map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        String value = r.getString(columnNumber);
        return value == null ? null : new Email(value);
    }
}
