package com.shahid.ecommerce.service;

import com.shahid.ecommerce.dto.product.ProductRequest;
import com.shahid.ecommerce.dto.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<ProductResponse> findAll(String search, Long categoryId, Pageable pageable);

    ProductResponse findById(Long id);

    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);
}
