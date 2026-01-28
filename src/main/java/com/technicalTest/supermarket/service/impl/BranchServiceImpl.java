package com.technicalTest.supermarket.service.impl;

import com.technicalTest.supermarket.dto.BranchDTO;
import com.technicalTest.supermarket.entity.Branch;
import com.technicalTest.supermarket.exception.NotFoundException;
import com.technicalTest.supermarket.mapper.BranchMapper;
import com.technicalTest.supermarket.repository.BranchRepository;
import com.technicalTest.supermarket.service.BranchService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository repository;
    private final BranchMapper mapper;

    @Override
    public List<BranchDTO> getBranches() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    public BranchDTO createBranch(BranchDTO branchDTO) {
        return mapper.toDTO(repository.save(mapper.toEntity(branchDTO)));
    }

    @Override
    public BranchDTO updateBranch(Long id, BranchDTO branchDTO) {
        Branch branch = repository.findById(id).orElseThrow(()
                -> new NotFoundException("la sucursal no existe."));
        mapper.update(branchDTO, branch);
        return mapper.toDTO(repository.save(branch));
    }

    @Override
    public void deleteBranch(Long id) {
        repository.deleteById(id);
    }
}
