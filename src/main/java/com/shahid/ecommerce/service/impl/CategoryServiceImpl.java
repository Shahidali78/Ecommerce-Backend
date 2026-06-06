package com.shahid.ecommerce.service.impl;

import com.shahid.ecommerce.dto.category.CategoryRequest;
import com.shahid.ecommerce.dto.category.CategoryResponse;
import com.shahid.ecommerce.exception.ConflictException;
import com.shahid.ecommerce.exception.ResourceNotFoundException;
import com.shahid.ecommerce.model.Category;
import com.shahid.ecommerce.repository.CategoryRepository;
import com.shahid.ecommerce.repository.ProductRepository;
import com.shahid.ecommerce.service.CategoryService;
import com.shahid.ecommerce.service.DtoMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            ProductRepository productRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll(Sort.by("name")).stream()
                .map(DtoMapper::toCategory)
                .toList();
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String name = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("A category with this name already exists");
        }
        Category category = new Category();
        apply(category, request);
        return DtoMapper.toCategory(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = requireCategory(id);
        String name = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ConflictException("A category with this name already exists");
        }
        apply(category, request);
        return DtoMapper.toCategory(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = requireCategory(id);
        if (productRepository.existsByCategoryId(id)) {
            throw new ConflictException("Delete or move this category's products first");
        }
        categoryRepository.delete(category);
    }

    private Category requireCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private void apply(Category category, CategoryRequest request) {
        category.setName(request.name().trim());
        category.setDescription(trimToNull(request.description()));
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
