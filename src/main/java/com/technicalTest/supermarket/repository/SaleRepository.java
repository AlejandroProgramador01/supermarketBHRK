package com.technicalTest.supermarket.repository;

import com.technicalTest.supermarket.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}
