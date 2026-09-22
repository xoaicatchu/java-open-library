package com.example.mybatis.handler;

import com.example.mybatis.entity.ProductStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(ProductStatus.class)
public class ProductStatusTypeHandler extends BaseTypeHandler<ProductStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ProductStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public ProductStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String status = rs.getString(columnName);
        return status != null ? ProductStatus.valueOf(status) : null;
    }

    @Override
    public ProductStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String status = rs.getString(columnIndex);
        return status != null ? ProductStatus.valueOf(status) : null;
    }

    @Override
    public ProductStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String status = cs.getString(columnIndex);
        return status != null ? ProductStatus.valueOf(status) : null;
    }
}
