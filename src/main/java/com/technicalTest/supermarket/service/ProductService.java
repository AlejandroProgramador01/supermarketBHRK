package com.technicalTest.supermarket.service;

import com.technicalTest.supermarket.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductDTO> getProducts(Pageable pageable);
    Page<ProductDTO> getDeletedProducts(Pageable pageable);
    ProductDTO getProductById(Long id);
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
    ProductDTO restoreProduct(Long id);
}
