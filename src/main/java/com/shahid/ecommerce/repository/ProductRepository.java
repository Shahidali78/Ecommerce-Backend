package com.shahid.ecommerce.repository;

import com.shahid.ecommerce.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            select p from Product p
            where p.active = true
              and (:categoryId is null or p.category.id = :categoryId)
              and (:search is null
                   or lower(p.name) like lower(concat('%', :search, '%'))
                   or lower(p.description) like lower(concat('%', :search, '%')))
            """)
    Page<Product> searchActive(
            @Param("search") String search,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    Optional<Product> findByIdAndActiveTrue(Long id);

    boolean existsByCategoryId(Long categoryId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}
