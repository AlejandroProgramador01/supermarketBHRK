package com.technicalTest.supermarket.mapper;

import com.technicalTest.supermarket.dto.BranchDTO;
import com.technicalTest.supermarket.entity.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    @Mapping(target = "id", ignore = true)
    void update(BranchDTO dto, @MappingTarget Branch branch);
    Branch toEntity(BranchDTO dto);
    BranchDTO toDTO(Branch branch);
}
