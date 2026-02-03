package com.technicalTest.supermarket.mapper;

import com.technicalTest.supermarket.dto.ProductDTO;
import com.technicalTest.supermarket.entity.Product;
import org.mapstruct.*;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy
        .ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductMapper{
    void updateEntity(ProductDTO dto, @MappingTarget Product product);
    Product toEntity(ProductDTO dto);
    ProductDTO toDto(Product product);
}

