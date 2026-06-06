package com.shahid.ecommerce.service.impl;

import com.shahid.ecommerce.dto.product.ProductRequest;
import com.shahid.ecommerce.dto.product.ProductResponse;
import com.shahid.ecommerce.exception.ResourceNotFoundException;
import com.shahid.ecommerce.model.Category;
import com.shahid.ecommerce.model.Product;
import com.shahid.ecommerce.repository.CategoryRepository;
import com.shahid.ecommerce.repository.ProductRepository;
import com.shahid.ecommerce.service.DtoMapper;
import com.shahid.ecommerce.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(String search, Long categoryId, Pageable pageable) {
        String normalizedSearch = search == null || search.isBlank() ? null : search.trim();
        return productRepository.searchActive(normalizedSearch, categoryId, pageable)
                .map(DtoMapper::toProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return DtoMapper.toProduct(product);
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        return DtoMapper.toProduct(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        apply(product, request);
        product.setActive(true);
        return DtoMapper.toProduct(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setActive(false);
        productRepository.save(product);
    }

    private void apply(Product product, ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        product.setName(request.name().trim());
        product.setDescription(request.description().trim());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setImageUrl(trimToNull(request.imageUrl()));
        product.setCategory(category);
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
