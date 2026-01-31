package com.technicalTest.supermarket.service.impl;

import com.technicalTest.supermarket.dto.ProductDTO;
import com.technicalTest.supermarket.entity.Product;
import com.technicalTest.supermarket.exception.NotFoundException;
import com.technicalTest.supermarket.mapper.ProductMapper;
import com.technicalTest.supermarket.repository.ProductRepository;
import com.technicalTest.supermarket.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public Page<ProductDTO> getProducts(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = repository.findById(id).orElseThrow(()
                -> new NotFoundException("El producto no existe"));

        if (product.isDeleted()) {
            throw new NotFoundException("El producto no existe");
        }

        return mapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        return mapper.toDto(repository.save(mapper.toEntity(productDTO)));
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product product = repository.findById(id).orElseThrow(()
                -> new NotFoundException("El producto no existe"));
        mapper.updateEntity(dto, product);
        return mapper.toDto(repository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("El producto no existe"));
        repository.delete(product);
    }
}
