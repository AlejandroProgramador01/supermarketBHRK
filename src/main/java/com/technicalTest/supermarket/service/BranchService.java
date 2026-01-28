package com.technicalTest.supermarket.service;

import com.technicalTest.supermarket.dto.BranchDTO;
import com.technicalTest.supermarket.exception.NotFoundException;

import java.util.List;

public interface BranchService {
    List<BranchDTO> getBranches();
    BranchDTO createBranch(BranchDTO branchDTO);
    BranchDTO updateBranch(Long id, BranchDTO branchDTO);
    void deleteBranch(Long id);
}
