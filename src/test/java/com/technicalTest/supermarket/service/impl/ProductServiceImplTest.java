package com.technicalTest.supermarket.service.impl;

import com.technicalTest.supermarket.dto.ProductDTO;
import com.technicalTest.supermarket.entity.Product;
import com.technicalTest.supermarket.exception.NotFoundException;
import com.technicalTest.supermarket.mapper.ProductMapperImpl;
import com.technicalTest.supermarket.mockFactory.MockFactory;
import com.technicalTest.supermarket.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
    void shouldCreateProduct(){
        //given
        ProductDTO dto = MockFactory.buildProductRequestDTO();
        Product product = MockFactory.buildProduct();
        LocalDateTime registrationDate = LocalDateTime.of(2026, 1, 1, 1, 1, 1);
        LocalDateTime modificationDate = LocalDateTime.of(2026, 1, 1, 1, 1, 1);

        //when
        when(repository.save(any(Product.class))).thenReturn(product);

        //then
        var created = productServiceImpl.createProduct(dto);
        assertThat(created)
                .extracting(
                        ProductDTO::getId,
                        ProductDTO::getName,
                        ProductDTO::getPrice
                ).containsExactly(
                        product.getId(),
                        product.getName(),
                        product.getPrice()
                );

        assertEquals(registrationDate, product.getRegistrationDate());
        assertEquals(modificationDate, product.getModificationDate());
        verify(repository, times(1)).save(any(Product.class));
    }


    @Test
    void shouldUpdateProduct() {

        //given
        Product product = MockFactory.buildProduct();
        ProductDTO dto = MockFactory.buildProductRequestDTO();
        LocalDateTime registrationDate = LocalDateTime.of(2026, 1, 1, 1, 1, 1);
        LocalDateTime modificationDate = LocalDateTime.of(2026, 1, 1, 1, 1, 1);
        product.setName("agua");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setRegistrationDate(registrationDate);
        product.setModificationDate(modificationDate);

        //when
        when(repository.findById(product.getId())).thenReturn(Optional.of(product));
        when(repository.save(product)).thenReturn(product);

        //then
        var updated = productServiceImpl.updateProduct(product.getId(), dto);
        assertThat(updated)
                .extracting(
                        ProductDTO::getId,
                        ProductDTO::getName,
                        ProductDTO::getPrice
                ).containsExactly(
                        product.getId(),
                        product.getName(),
                        product.getPrice()
                );

        assertEquals(registrationDate, product.getRegistrationDate());
        assertEquals(modificationDate, product.getModificationDate());
        verify(repository).findById(product.getId());
        verify(repository, times(1)).save(any(Product.class));
    }


    @Test
    void shouldThrowErrorWhenProductIsNotFoundForUpdate() {
        // given
        ProductDTO dto = MockFactory.buildProductRequestDTO();

        // when
        when(repository.findById(dto.getId())).thenReturn(Optional.empty());

        // then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productServiceImpl.updateProduct(dto.getId(), dto)
        );

        assertEquals("the product does not exist", exception.getMessage());
        verify(repository, never()).save(any(Product.class));
    }


    @Test
    void shouldDeleteProduct() {
        // given
        Product product = MockFactory.buildProduct();

        // when
        when(repository.findById(product.getId())).thenReturn(Optional.of(product));

        // then
        productServiceImpl.deleteProduct(product.getId());
        verify(repository, times(1)).findById(product.getId());
        verify(repository, times(1)).save(product);
    }


    @Test
    void shouldGetProductById() {
        // given
        Product product = MockFactory.buildProduct();

        // when
        when(repository.findById(product.getId())).thenReturn(Optional.of(product));

        // then
        ProductDTO result = productServiceImpl.getProductById(product.getId());
        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getPrice(), result.getPrice());
        assertEquals(product.getRegistrationDate(), result.getRegistrationDate());
        assertEquals(product.getModificationDate(), result.getModificationDate());

        verify(repository, times(1)).findById(product.getId());
    }


    @Test
    void shouldThrowErrorWhenProductIsNotFoundForGetById() {
        //given
        Product product = MockFactory.buildProduct();

        //when
        when(repository.findById(product.getId())).thenReturn(Optional.empty());

        //then
        var error = assertThrows(NotFoundException.class, () -> productServiceImpl.getProductById(product.getId()));
        assertEquals("the product does not exist", error.getMessage());
    }


    @Test
    void shouldGetProducts() {
        // given
        Pageable pageable = MockFactory.buildPageable();
        Page<Product> productPage = MockFactory.getProductsPage(pageable);
        LocalDateTime registrationDate = LocalDateTime.of(2026, 1, 1, 1, 1, 1);
        LocalDateTime modificationDate = LocalDateTime.of(2026, 1, 1, 1, 1, 1);

        // when
        when(repository.findAll(pageable)).thenReturn(productPage);

        // then
        Page<ProductDTO> products = productServiceImpl.getProducts(pageable);
        assertNotNull(products);
        assertEquals(1, products.getTotalElements());
        assertEquals(1L, products.getContent().get(0).getId());
        assertEquals("agua", products.getContent().get(0).getName());
        assertEquals(BigDecimal.valueOf(1000), products.getContent().get(0).getPrice());
        assertEquals(registrationDate, products.getContent().get(0).getRegistrationDate());
        assertEquals(modificationDate, products.getContent().get(0).getModificationDate());
        verify(repository, times(1)).findAll(pageable);
    }


    @Test
    void shouldGetDeleteProducts() {
        // given
        Pageable pageable = MockFactory.buildPageable();
        Page<Product> productPage = MockFactory.getProductsPage(pageable);
        LocalDateTime registrationDate = LocalDateTime.of(2026, 1, 1, 1, 1, 1);
        LocalDateTime modificationDate = LocalDateTime.of(2026, 1, 1, 1, 1, 1);
        productPage.getContent().forEach(product -> product.setDeleted(true));

        // when
        when(repository.findAllDeleted(pageable)).thenReturn(productPage);

        // then
        Page<ProductDTO> products = productServiceImpl.getDeletedProducts(pageable);
        assertNotNull(products);
        assertEquals(1, products.getTotalElements());
        assertEquals(1L, products.getContent().get(0).getId());
        assertEquals("agua", products.getContent().get(0).getName());
        assertEquals(BigDecimal.valueOf(1000), products.getContent().get(0).getPrice());
        assertEquals(registrationDate, products.getContent().get(0).getRegistrationDate());
        assertEquals(modificationDate, products.getContent().get(0).getModificationDate());
        verify(repository, times(1)).findAllDeleted(pageable);
    }


    @Test
    void shouldRestoreProduct(){
        //given
        Product product = MockFactory.buildProduct();

        //when
        when(repository.findDeleted(product.getId())).thenReturn(Optional.of(product));

        //then
        productServiceImpl.restoreProduct(product.getId());
        verify(repository, times(1)).findDeleted(product.getId());
        verify(repository, times(1)).restore(product.getId());
    }

    @Test
    void shouldThrowErrorWhenProductIsNotFoundForRestore() {
        //given
        Product product = MockFactory.buildProduct();

        //when
        when(repository.findDeleted(product.getId())).thenReturn(Optional.empty());

        //then
        var error = assertThrows(NotFoundException.class, () -> productServiceImpl.restoreProduct(product.getId()));
        assertEquals("the product has not been deleted", error.getMessage());
    }
}
