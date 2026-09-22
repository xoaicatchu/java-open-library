package com.example.jooq.service;

import com.example.jooq.dto.*;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.jooq.impl.DSL.*;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {
    
    private final DSLContext dsl;
    
    private final Table<org.jooq.Record> categories = table("categories");
    private final Table<org.jooq.Record> products = table("products");
    private final Table<org.jooq.Record> sales = table("sales");
    
    private final Field<Integer> catId = field("categories.id", Integer.class);
    private final Field<String> catName = field("categories.name", String.class);
    
    private final Field<Integer> prodId = field("products.id", Integer.class);
    private final Field<String> prodName = field("products.name", String.class);
    private final Field<Integer> prodCatId = field("products.category_id", Integer.class);
    private final Field<BigDecimal> prodPrice = field("products.price", BigDecimal.class);
    
    private final Field<Integer> saleId = field("sales.id", Integer.class);
    private final Field<Integer> saleProdId = field("sales.product_id", Integer.class);
    private final Field<Integer> saleQty = field("sales.quantity", Integer.class);
    private final Field<LocalDate> saleDate = field("sales.sale_date", LocalDate.class);
    private final Field<BigDecimal> saleRev = field("sales.revenue", BigDecimal.class);

    public AnalyticsService(DSLContext dsl) {
        this.dsl = dsl;
    }

    // 1. DSL Query Builder - Truy vấn cơ bản (basic select)
    public List<ProductDto> getAllProducts() {
        return dsl.select(prodId, prodName, prodCatId, prodPrice)
                .from(products)
                .fetchInto(ProductDto.class);
    }
    
    // 2. Joins & Aggregation - Gom nhóm và tính tổng doanh thu theo danh mục
    public List<CategoryRevenueDto> getRevenueByCategory() {
        return dsl.select(catName, sum(saleRev).as("totalRevenue"))
                .from(categories)
                .join(products).on(catId.eq(prodCatId))
                .join(sales).on(prodId.eq(saleProdId))
                .groupBy(catName)
                .orderBy(sum(saleRev).desc())
                .fetchInto(CategoryRevenueDto.class);
    }
    
    // 3. Window Functions - Xếp hạng sản phẩm dựa trên doanh thu
    public List<ProductRankDto> getProductRankingByRevenue() {
        Field<BigDecimal> totalRev = sum(saleRev).as("totalRevenue");
        return dsl.select(
                    prodId, 
                    prodName, 
                    totalRev,
                    rank().over(orderBy(sum(saleRev).desc())).as("rank")
                )
                .from(products)
                .join(sales).on(prodId.eq(saleProdId))
                .groupBy(prodId, prodName)
                .orderBy(sum(saleRev).desc())
                .fetchInto(ProductRankDto.class);
    }
    
    // 4. CTE (Common Table Expressions) - Lấy các sản phẩm có doanh thu cao
    public List<ProductDto> getTopPerformingProducts() {
        Name cteName = name("TopProducts");
        Field<Integer> cteProdId = field(name("TopProducts", "prod_id"), Integer.class);
        Field<BigDecimal> cteRev = field(name("TopProducts", "total_rev"), BigDecimal.class);
        
        return dsl.with(cteName).as(
                    select(saleProdId.as("prod_id"), sum(saleRev).as("total_rev"))
                    .from(sales)
                    .groupBy(saleProdId)
                    .having(sum(saleRev).gt(new BigDecimal("500")))
                )
                .select(prodId, prodName, prodCatId, prodPrice)
                .from(products)
                .join(table(cteName)).on(prodId.eq(cteProdId))
                .orderBy(cteRev.desc())
                .fetchInto(ProductDto.class);
    }
    
    // 5. Batch Operations - Thêm mới hàng loạt (Batch insert) để tối ưu hiệu suất
    @Transactional
    public void batchInsertSales(List<SaleDto> newSales) {
        Field<Integer> pId = field("product_id", Integer.class);
        Field<Integer> qty = field("quantity", Integer.class);
        Field<LocalDate> sDate = field("sale_date", LocalDate.class);
        Field<BigDecimal> rev = field("revenue", BigDecimal.class);
        
        BatchBindStep batch = dsl.batch(
            insertInto(sales, pId, qty, sDate, rev)
            .values((Integer) null, null, null, null)
        );
        
        for (SaleDto sale : newSales) {
            batch.bind(sale.productId(), sale.quantity(), sale.saleDate(), sale.revenue());
        }
        
        batch.execute();
    }
    
    // 6. Plain SQL - Chạy câu lệnh SQL thuần
    public List<ProductDto> findProductsByRawSql(String keyword) {
        return dsl.resultQuery("SELECT * FROM products WHERE name LIKE ?", "%" + keyword + "%")
                .fetchInto(ProductDto.class);
    }

    // 7. Transaction management với DSLContext - Quản lý giao dịch
    @Transactional
    public void updateProductPriceAndInsertAudit(Integer id, BigDecimal newPrice) {
        dsl.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            
            // Cập nhật giá sản phẩm
            int updated = ctx.update(products)
               .set(prodPrice, newPrice)
               .where(prodId.eq(id))
               .execute();
               
            if (updated > 0) {
                // Ghi log hoặc thêm dữ liệu liên quan
                ctx.insertInto(table("categories"), field("name"))
                   .values("PriceUpdate_" + id)
                   .execute();
            } else {
                throw new RuntimeException("Product not found");
            }
        });
    }

    // 8. Delete - Xóa bản ghi
    @Transactional
    public int deleteSalesForProduct(Integer id) {
        return dsl.deleteFrom(sales)
                .where(saleProdId.eq(id))
                .execute();
    }
}
