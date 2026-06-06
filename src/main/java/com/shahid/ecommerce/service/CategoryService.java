package com.shahid.ecommerce.service;

import com.shahid.ecommerce.dto.category.CategoryRequest;
import com.shahid.ecommerce.dto.category.CategoryResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> findAll();

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    void delete(Long id);
}
