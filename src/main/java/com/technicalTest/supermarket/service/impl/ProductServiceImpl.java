package com.technicalTest.supermarket.service.impl;

import com.technicalTest.supermarket.dto.ProductDTO;
import com.technicalTest.supermarket.entity.Product;
import com.technicalTest.supermarket.exception.NotFoundException;
import com.technicalTest.supermarket.mapper.ProductMapper;
import com.technicalTest.supermarket.repository.ProductRepository;
import com.technicalTest.supermarket.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {


    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public List<ProductDTO> getProducts() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        return mapper.toDto(repository.save(mapper.toEntity(productDTO)));
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = repository.findById(id).orElseThrow(()
                -> new NotFoundException("El producto no exíste."));
        mapper.update(productDTO, product);
        return mapper.toDto(repository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }
}
