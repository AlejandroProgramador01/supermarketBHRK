package com.technicalTest.supermarket.repository;

import com.technicalTest.supermarket.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {
}
