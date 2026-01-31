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
        ProductDTO requestDTO = MockFactory.buildProductRequestDTO();
        Product product = MockFactory.buildProduct();
        LocalDateTime registrationDate = LocalDateTime.of(2026, 1, 1, 1, 1, 1);
        LocalDateTime modificationDate = LocalDateTime.of(2026, 1, 1, 1, 1, 1);

        //when
        when(repository.save(any(Product.class))).thenReturn(product);

        //then
        var created = productServiceImpl.createProduct(requestDTO);
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
        Long id = 1L;
        Product product = MockFactory.buildProduct();
        ProductDTO dto = MockFactory.buildProductRequestDTO();
        LocalDateTime registrationDate = LocalDateTime.of(2026, 1, 1, 1, 1, 1);
        LocalDateTime modificationDate = LocalDateTime.of(2026, 1, 1, 1, 1, 1);
        product.setId(id);
        product.setName("agua");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setRegistrationDate(registrationDate);
        product.setModificationDate(modificationDate);

        //when
        when(repository.findById(id)).thenReturn(Optional.of(product));
        when(repository.save(product)).thenReturn(product);

        //then
        var updated = productServiceImpl.updateProduct(id, dto);
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
        verify(repository).findById(id);
        verify(repository, times(1)).save(any(Product.class));
    }


    @Test
    void shouldThrowErrorWhenProductIsNotFoundForUpdate() {
        // given
        Long id = 1L;
        ProductDTO dto = MockFactory.buildProductRequestDTO();

        // when
        when(repository.findById(id)).thenReturn(Optional.empty());

        // then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productServiceImpl.updateProduct(id, dto)
        );

        assertEquals("El producto no existe", exception.getMessage());
        verify(repository, never()).save(any(Product.class));
    }



    @Test
    void shouldDeleteProduct() {
        // given
        Long id = 1L;
        Product product = MockFactory.buildProduct();

        // when
        when(repository.findById(id)).thenReturn(Optional.of(product));

        // then
        productServiceImpl.deleteProduct(id);
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).delete(product);
        verify(repository, never()).save(any());
    }


    @Test
    void shouldThrowErrorWhenProductIsNotFoundForDelete() {
        // given
        Long id = 1L;

        //when
        when(repository.findById(id)).thenReturn(Optional.empty());

        // then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productServiceImpl.deleteProduct(id)
        );

        assertEquals("El producto no existe", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowErrorWhenProductIsDeleted() {
        // given
        Long id = 1L;
        Product product = MockFactory.buildDeletedProduct();

        //when
        when(repository.findById(id)).thenReturn(Optional.of(product));

        // then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productServiceImpl.getProductById(id)
        );

        assertEquals("El producto no existe", exception.getMessage());
        verify(repository, times(1)).findById(id);
    }


    @Test
    void shouldGetProductById() {
        // given
        Long id = 1L;
        Product product = MockFactory.buildProduct();
        product.setDeleted(false);

        when(repository.findById(id)).thenReturn(Optional.of(product));

        // when
        ProductDTO result = productServiceImpl.getProductById(id);

        // then
        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getPrice(), result.getPrice());
        assertEquals(product.getRegistrationDate(), result.getRegistrationDate());
        assertEquals(product.getModificationDate(), result.getModificationDate());

        verify(repository, times(1)).findById(id);
    }



    @Test
    void shouldThrowErrorWhenProductIsNotFoundForGetById() {
        //given
        Long id = 1L;

        //when
        when(repository.findById(id)).thenReturn(Optional.empty());

        //then
        var error = assertThrows(NotFoundException.class, () -> productServiceImpl.getProductById(id));
        assertEquals("El producto no existe", error.getMessage());
        verify(repository, times(0)).save(any(Product.class));
    }


    @Test
    void shouldGetAllProducts() {
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
}
