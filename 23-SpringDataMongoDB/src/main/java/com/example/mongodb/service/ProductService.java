package com.example.mongodb.service;

import com.example.mongodb.dto.CategoryCountDto;
import com.example.mongodb.dto.ProductAvgRatingDto;
import com.example.mongodb.entity.Product;
import com.example.mongodb.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;

    public ProductService(ProductRepository productRepository, MongoTemplate mongoTemplate) {
        this.productRepository = productRepository;
        this.mongoTemplate = mongoTemplate;
    }

    // CRUD - Create
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // CRUD - Read
    public Optional<Product> getProduct(String id) {
        return productRepository.findById(id);
    }

    // CRUD - Update
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    // CRUD - Delete
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
    
    // Repository Custom Query
    public List<Product> findByCategoryAndMinPrice(String category, double minPrice) {
        return productRepository.findByCategoryAndMinPrice(category, minPrice);
    }

    // MongoTemplate - Programmatic queries, Criteria API
    public List<Product> findProductsByTag(String tag) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tags").in(tag));
        return mongoTemplate.find(query, Product.class);
    }

    // Text search - Full-text search
    public List<Product> searchProducts(String keyword) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(keyword);
        Query query = TextQuery.queryText(criteria).sortByScore();
        return mongoTemplate.find(query, Product.class);
    }

    // Aggregation - Products count per category
    public List<CategoryCountDto> getProductsCountPerCategory() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.group("category").count().as("count"),
                Aggregation.sort(Sort.Direction.DESC, "count")
        );
        
        AggregationResults<CategoryCountDto> results = mongoTemplate.aggregate(agg, "products", CategoryCountDto.class);
        return results.getMappedResults();
    }

    // Aggregation - Avg rating per product
    public List<ProductAvgRatingDto> getAverageRatingPerProduct() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.unwind("reviews"),
                Aggregation.group("_id").avg("reviews.rating").as("avgRating"),
                Aggregation.sort(Sort.Direction.DESC, "avgRating")
        );
        
        AggregationResults<ProductAvgRatingDto> results = mongoTemplate.aggregate(agg, "products", ProductAvgRatingDto.class);
        return results.getMappedResults();
    }
}
