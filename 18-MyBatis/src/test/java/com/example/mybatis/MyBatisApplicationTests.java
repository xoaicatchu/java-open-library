package com.example.mybatis;

import com.example.mybatis.entity.Category;
import com.example.mybatis.entity.Product;
import com.example.mybatis.entity.ProductStatus;
import com.example.mybatis.mapper.CategoryMapper;
import com.example.mybatis.mapper.ProductMapper;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MyBatisApplicationTests {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private MockMvc mockMvc;

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

    @Test
    void testControllerGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/products?page=0&size=5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testControllerSearchDynamic() throws Exception {
        mockMvc.perform(get("/api/products/search?name=Laptop&minPrice=1000"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Laptop"))
            .andExpect(jsonPath("$[0].category.name").value("Electronics"));
    }

    @Test
    void testControllerCreateAndGetProduct() throws Exception {
        String json = """
            {
                "name": "Wireless Mouse",
                "price": 29.99,
                "categoryId": 1,
                "status": "ACTIVE"
            }
            """;
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Wireless Mouse"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testControllerCategoryEndpoints() throws Exception {
        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());

        String newCat = """
            {
                "name": "Gaming",
                "description": "Gaming hardware"
            }
            """;
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newCat))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Gaming"));
    }
}
