package com.technicalTest.supermarket.mockFactory;

import com.technicalTest.supermarket.dto.*;
import com.technicalTest.supermarket.entity.Product;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class MockFactory {

    public static ProductDTO buildProductRequestDTO(){
        return ProductDTO.builder()
                .id(1L)
                .name("agua")
                .price(BigDecimal.valueOf(1000))
                .build();
    }

    public static ProductDTO buildProductResponseDTO(){
        return ProductDTO.builder()
                .id(1L)
                .name("agua")
                .price(BigDecimal.valueOf(1000))
                .registrationDate(LocalDateTime.of(2026, 1, 1, 1, 1, 1))
                .modificationDate(LocalDateTime.of(2026, 1, 1, 1, 1, 1))
                .build();
    }

    public static Product buildProduct() {
        return Product.builder()
                .id(1L)
                .name("agua")
                .price(BigDecimal.valueOf(1000))
                .registrationDate(LocalDateTime.of(2026, 1, 1, 1, 1, 1))
                .modificationDate(LocalDateTime.of(2026, 1, 1, 1, 1, 1))
                .deleted(false)
                .build();
    }

    public static Pageable buildPageable() {
        return PageRequest.of(0, 10, Sort.by("id").ascending());
    }

    public static List<Product> getProductsList() {
        return new ArrayList<>(List.of(buildProduct()));
    }

    public static Page<Product> getProductsPage(Pageable pageable) {
        List<Product> products = getProductsList();
        return new PageImpl<>(products, pageable, products.size());
    }

    public static Page<ProductDTO> getProductsDTOPage(Pageable pageable) {
        List<ProductDTO> products = new ArrayList<>(List.of(buildProductResponseDTO()));
        return new PageImpl<>(products, pageable, products.size());
    }

    public static Product buildDeletedProduct() {
        Product product = buildProduct();
        product.setDeleted(true);
        return product;
    }

}
