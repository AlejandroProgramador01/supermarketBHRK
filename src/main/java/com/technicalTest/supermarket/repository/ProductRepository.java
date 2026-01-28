package com.technicalTest.supermarket.repository;

import com.technicalTest.supermarket.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
