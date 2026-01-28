package com.technicalTest.supermarket.service.impl;

import com.technicalTest.supermarket.dto.BranchDTO;
import com.technicalTest.supermarket.dto.ProductDTO;
import com.technicalTest.supermarket.entity.Branch;
import com.technicalTest.supermarket.entity.Product;
import com.technicalTest.supermarket.exception.NotFoundException;
import com.technicalTest.supermarket.mapper.ProductMapperImpl;
import com.technicalTest.supermarket.mockFactory.MockFactory;
import com.technicalTest.supermarket.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Import({ProductMapperImpl.class})
@ExtendWith(SpringExtension.class)
public class ProductServiceImplTest {
    @Autowired
    private ProductMapperImpl mapper;
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @BeforeEach
    void setUp() {
        this.repository = mock(ProductRepository.class);
        this.productServiceImpl = new ProductServiceImpl(repository, mapper);
    }

    @Test
    public void shouldGetProducts(){
        //given
        List<Product> mockProducts = MockFactory.getProducts();

        //when
        when(repository.findAll())
                .thenReturn(MockFactory.getProducts());

        //then
        List<ProductDTO> product = productServiceImpl.getProducts();
        assertEquals(1, product.size());

        var firstBranch = product.get(0);
        assertThat(firstBranch)
                .extracting(
                        ProductDTO::getId,
                        ProductDTO::getName,
                        ProductDTO::getCategory,
                        ProductDTO::getPrice,
                        ProductDTO::getStock
                ).containsExactly(
                        1L,
                        "agua",
                        "bebidas",
                        BigDecimal.valueOf(1000),
                        100
                );

        verify(repository).findAll();
    }

    @Test
    void shouldCreateProduct(){
        //given
        ProductDTO mockProductRequestDto = MockFactory.buildProductDtoRequestDto();
        Product mockProduct = MockFactory.buildProductEntity();

        //when
        when(repository.save(any(Product.class))).thenReturn(mockProduct);

        //then
        var created = productServiceImpl.createProduct(mockProductRequestDto);
        assertThat(created)
                .extracting(
                        ProductDTO::getId,
                        ProductDTO::getName,
                        ProductDTO::getCategory,
                        ProductDTO::getPrice,
                        ProductDTO::getStock
                ).containsExactly(
                        mockProduct.getId(),
                        mockProduct.getName(),
                        mockProduct.getCategory(),
                        mockProduct.getPrice(),
                        mockProduct.getStock()
                );

        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldUpdateProduct() {
        //given
        Long id = 1L;
        Product mockProduct = MockFactory.buildProductEntity();
        Product mockUpdatedProduct = MockFactory.buildProductEntity();
        ProductDTO mockProductRequestDto = MockFactory.buildProductDtoRequestDto();
        mockUpdatedProduct.setId(id);
        mockUpdatedProduct.setName(mockProductRequestDto.getName());
        mockUpdatedProduct.setCategory(mockProductRequestDto.getCategory());
        mockUpdatedProduct.setPrice(mockProductRequestDto.getPrice());
        mockUpdatedProduct.setStock(mockProductRequestDto.getStock());

        //when
        when(repository.findById(id)).thenReturn(Optional.of(mockUpdatedProduct));
        when(repository.save(mockUpdatedProduct)).thenReturn(mockUpdatedProduct);

        //then
        var updated = productServiceImpl.updateProduct(id, mockProductRequestDto);
        assertThat(updated)
                .extracting(
                        ProductDTO::getId,
                        ProductDTO::getName,
                        ProductDTO::getCategory,
                        ProductDTO::getPrice,
                        ProductDTO::getStock
                ).containsExactly(
                        mockUpdatedProduct.getId(),
                        mockUpdatedProduct.getName(),
                        mockUpdatedProduct.getCategory(),
                        mockUpdatedProduct.getPrice(),
                        mockUpdatedProduct.getStock()
                );

        verify(repository).findById(id);
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldThrowErrorWhenProductIsNotFoundForUpdate() {
        //given
        Long id = 1L;
        ProductDTO mockProductRequestDto = MockFactory.buildProductDtoRequestDto();

        //when
        when(repository.findById(id)).thenReturn(Optional.empty());

        //then
        var error = assertThrows(NotFoundException.class, () -> productServiceImpl.updateProduct(id, mockProductRequestDto));
        assertEquals("El producto no exíste.", error.getMessage());

        verify(repository, times(0)).save(any(Product.class));
    }

    @Test
    void shouldDeleteProduct() {
        // given
        Long id = 1L;

        // when
        productServiceImpl.deleteProduct(id);

        // then
        verify(repository,times(1)).deleteById(id);
    }
}
