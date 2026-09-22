package com.example.mybatis.service;

import com.example.mybatis.dto.CategoryResponse;
import com.example.mybatis.dto.CreateCategoryRequest;
import com.example.mybatis.entity.Category;
import com.example.mybatis.mapper.CategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryResponse> findAll() {
        return categoryMapper.findAll().stream()
            .map(CategoryResponse::from)
            .toList();
    }

    public CategoryResponse findById(Long id) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new IllegalArgumentException("Không tìm thấy danh mục với id: " + id);
        }
        return CategoryResponse.from(category);
    }

    @Transactional
    public CategoryResponse create(CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        categoryMapper.insert(category);
        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        categoryMapper.delete(id);
    }
}
