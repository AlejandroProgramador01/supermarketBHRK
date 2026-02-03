package com.technicalTest.supermarket.repository;

import com.technicalTest.supermarket.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.deleted = false")
    Page<Product> findAll(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.deleted = false")
    Optional<Product> findById(@Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.deleted = true")
    Optional<Product> findDeleted(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.id = :id AND p.deleted = false")
    boolean existsById(@Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE p.deleted = true")
    Page<Product> findAllDeleted(Pageable pageable);

    @Modifying
    @Query("UPDATE Product p SET p.deleted = false WHERE p.id = :id")
    void restore(@Param("id") Long id);
}
