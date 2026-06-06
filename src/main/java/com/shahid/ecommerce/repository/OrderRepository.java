package com.shahid.ecommerce.repository;

import com.shahid.ecommerce.model.CustomerOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    @Override
    @EntityGraph(attributePaths = {"user", "items"})
    Page<CustomerOrder> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "items"})
    Page<CustomerOrder> findAllByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "items"})
    @Query("select o from CustomerOrder o where o.id = :id")
    Optional<CustomerOrder> findWithItemsById(@Param("id") Long id);
}
