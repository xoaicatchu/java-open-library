import os

project_dir = r"d:\GitHub\java-example\18-MyBatis"

files = {
    "pom.xml": """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.1</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>mybatis-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>mybatis-demo</name>
    <description>MyBatis Demo project for Spring Boot</description>
    <properties>
        <java.version>21</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>3.0.4</version>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter-test</artifactId>
            <version>3.0.4</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
""",
    ".mvn/wrapper/maven-wrapper.properties": """distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar
""",
    "src/main/resources/application.yml": """server:
  port: 8118
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  sql:
    init:
      mode: always
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.example.mybatis.entity
  type-handlers-package: com.example.mybatis.handler
  configuration:
    map-underscore-to-camel-case: true
""",
    "src/main/resources/schema.sql": """
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category_id BIGINT,
    status VARCHAR(50),
    FOREIGN KEY (category_id) REFERENCES category(id)
);
""",
    "src/main/resources/data.sql": """
INSERT INTO category (name, description) VALUES ('Electronics', 'Electronic items');
INSERT INTO category (name, description) VALUES ('Books', 'Books and reading materials');

INSERT INTO product (name, price, category_id, status) VALUES ('Laptop', 1200.00, 1, 'ACTIVE');
INSERT INTO product (name, price, category_id, status) VALUES ('Smartphone', 800.00, 1, 'ACTIVE');
INSERT INTO product (name, price, category_id, status) VALUES ('Novel', 15.00, 2, 'INACTIVE');
""",
    "src/main/java/com/example/mybatis/Application.java": """package com.example.mybatis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
""",
    "src/main/java/com/example/mybatis/entity/ProductStatus.java": """package com.example.mybatis.entity;

public enum ProductStatus {
    ACTIVE,
    INACTIVE,
    DISCONTINUED
}
""",
    "src/main/java/com/example/mybatis/handler/ProductStatusTypeHandler.java": """package com.example.mybatis.handler;

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
""",
    "src/main/java/com/example/mybatis/entity/Category.java": """package com.example.mybatis.entity;

public class Category {
    private Long id;
    private String name;
    private String description;

    public Category() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
""",
    "src/main/java/com/example/mybatis/entity/Product.java": """package com.example.mybatis.entity;

import java.math.BigDecimal;

public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
    private Long categoryId;
    private ProductStatus status;
    private Category category;

    public Product() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
""",
    "src/main/java/com/example/mybatis/mapper/CategoryMapper.java": """package com.example.mybatis.mapper;

import com.example.mybatis.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {
    @Select("SELECT * FROM category WHERE id = #{id}")
    Category findById(Long id);

    @Select("SELECT * FROM category")
    List<Category> findAll();

    @Insert("INSERT INTO category(name, description) VALUES(#{name}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Category category);

    @Update("UPDATE category SET name=#{name}, description=#{description} WHERE id=#{id}")
    void update(Category category);

    @Delete("DELETE FROM category WHERE id=#{id}")
    void delete(Long id);
}
""",
    "src/main/java/com/example/mybatis/mapper/ProductMapper.java": """package com.example.mybatis.mapper;

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
""",
    "src/main/resources/mapper/ProductMapper.xml": """<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.mybatis.mapper.ProductMapper">

    <resultMap id="ProductResultMap" type="Product">
        <id property="id" column="p_id"/>
        <result property="name" column="p_name"/>
        <result property="price" column="p_price"/>
        <result property="categoryId" column="p_category_id"/>
        <result property="status" column="p_status" typeHandler="com.example.mybatis.handler.ProductStatusTypeHandler"/>
        <association property="category" javaType="Category">
            <id property="id" column="c_id"/>
            <result property="name" column="c_name"/>
            <result property="description" column="c_description"/>
        </association>
    </resultMap>

    <select id="findById" resultType="Product">
        SELECT id, name, price, category_id, status FROM product WHERE id = #{id}
    </select>

    <select id="findWithCategory" resultMap="ProductResultMap">
        SELECT p.id as p_id, p.name as p_name, p.price as p_price, p.category_id as p_category_id, p.status as p_status,
               c.id as c_id, c.name as c_name, c.description as c_description
        FROM product p
        LEFT JOIN category c ON p.category_id = c.id
    </select>

    <select id="findDynamic" resultMap="ProductResultMap">
        SELECT p.id as p_id, p.name as p_name, p.price as p_price, p.category_id as p_category_id, p.status as p_status,
               c.id as c_id, c.name as c_name, c.description as c_description
        FROM product p
        LEFT JOIN category c ON p.category_id = c.id
        <where>
            <if test="name != null and name != ''">
                AND p.name LIKE CONCAT('%', #{name}, '%')
            </if>
            <if test="minPrice != null">
                AND p.price &gt;= #{minPrice}
            </if>
            <if test="maxPrice != null">
                AND p.price &lt;= #{maxPrice}
            </if>
            <if test="statusList != null and statusList.size() > 0">
                AND p.status IN
                <foreach item="item" collection="statusList" open="(" separator="," close=")">
                    #{item}
                </foreach>
            </if>
        </where>
    </select>

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO product (name, price, category_id, status)
        VALUES (#{name}, #{price}, #{categoryId}, #{status, typeHandler=com.example.mybatis.handler.ProductStatusTypeHandler})
    </insert>

    <update id="update">
        UPDATE product
        <set>
            <if test="name != null">name = #{name},</if>
            <if test="price != null">price = #{price},</if>
            <if test="categoryId != null">category_id = #{categoryId},</if>
            <if test="status != null">status = #{status, typeHandler=com.example.mybatis.handler.ProductStatusTypeHandler}</if>
        </set>
        WHERE id = #{id}
    </update>

    <delete id="delete">
        DELETE FROM product WHERE id = #{id}
    </delete>

</mapper>
""",
    "src/test/java/com/example/mybatis/MyBatisApplicationTests.java": """package com.example.mybatis;

import com.example.mybatis.entity.Category;
import com.example.mybatis.entity.Product;
import com.example.mybatis.entity.ProductStatus;
import com.example.mybatis.mapper.CategoryMapper;
import com.example.mybatis.mapper.ProductMapper;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyBatisApplicationTests {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Test
    void testCategoryFindAll() {
        List<Category> categories = categoryMapper.findAll();
        assertFalse(categories.isEmpty());
    }

    @Test
    void testCategoryInsertUpdateDelete() {
        Category cat = new Category();
        cat.setName("Test Cat");
        cat.setDescription("Test Desc");
        categoryMapper.insert(cat);
        assertNotNull(cat.getId());

        cat.setName("Updated Cat");
        categoryMapper.update(cat);
        Category found = categoryMapper.findById(cat.getId());
        assertEquals("Updated Cat", found.getName());

        categoryMapper.delete(cat.getId());
        assertNull(categoryMapper.findById(cat.getId()));
    }

    @Test
    void testProductFindById() {
        Product p = productMapper.findById(1L);
        assertNotNull(p);
        assertEquals("Laptop", p.getName());
        assertEquals(ProductStatus.ACTIVE, p.getStatus());
    }

    @Test
    void testProductInsertWithEnum() {
        Product p = new Product();
        p.setName("Tablet");
        p.setPrice(new BigDecimal("300.00"));
        p.setCategoryId(1L);
        p.setStatus(ProductStatus.INACTIVE);
        productMapper.insert(p);

        assertNotNull(p.getId());
        Product found = productMapper.findById(p.getId());
        assertEquals(ProductStatus.INACTIVE, found.getStatus());
    }

    @Test
    void testProductUpdateDynamic() {
        Product p = new Product();
        p.setId(2L);
        p.setPrice(new BigDecimal("850.00"));
        productMapper.update(p);

        Product found = productMapper.findById(2L);
        assertEquals(new BigDecimal("850.00"), found.getPrice());
        assertEquals("Smartphone", found.getName()); // Not updated
    }

    @Test
    void testProductDelete() {
        Product p = new Product();
        p.setName("Temp");
        p.setPrice(new BigDecimal("10.00"));
        p.setCategoryId(2L);
        p.setStatus(ProductStatus.ACTIVE);
        productMapper.insert(p);

        productMapper.delete(p.getId());
        assertNull(productMapper.findById(p.getId()));
    }

    @Test
    void testFindWithCategoryJoin() {
        RowBounds rowBounds = new RowBounds(0, 2);
        List<Product> products = productMapper.findWithCategory(rowBounds);
        assertNotNull(products);
        assertEquals(2, products.size());
        assertNotNull(products.get(0).getCategory());
        assertEquals("Electronics", products.get(0).getCategory().getName());
    }

    @Test
    void testFindDynamic() {
        List<Product> products = productMapper.findDynamic("Lap", null, null, Arrays.asList("ACTIVE", "INACTIVE"));
        assertFalse(products.isEmpty());
        assertEquals("Laptop", products.get(0).getName());
        
        List<Product> cheapProducts = productMapper.findDynamic(null, null, new BigDecimal("20.00"), null);
        assertEquals(1, cheapProducts.size());
        assertEquals("Novel", cheapProducts.get(0).getName());
    }
}
"""
}

for path, content in files.items():
    full_path = os.path.join(project_dir, path)
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    with open(full_path, "w", encoding="utf-8") as f:
        f.write(content)
print("done")
