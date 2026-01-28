package com.technicalTest.supermarket.mapper;

import com.technicalTest.supermarket.dto.ProductDTO;
import com.technicalTest.supermarket.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    void update(ProductDTO dto, @MappingTarget Product product);
    Product toEntity(ProductDTO dto);
    ProductDTO toDto(Product product);
}

