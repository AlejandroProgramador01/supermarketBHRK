package com.technicalTest.supermarket.controller;

import com.technicalTest.supermarket.dto.ProductDTO;
import com.technicalTest.supermarket.entity.Product;
import com.technicalTest.supermarket.exception.NotFoundException;
import com.technicalTest.supermarket.handler.GlobalExceptionHandler;
import com.technicalTest.supermarket.mockFactory.MockFactory;
import com.technicalTest.supermarket.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    private MockMvc mockMvc;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private ProductService productService;
    @InjectMocks
    private ProductController controller;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void getProducts() throws Exception {
        // given
        Pageable pageable = MockFactory.buildPageable();
        Page<ProductDTO> productsPage = MockFactory.getProductsDTOPage(pageable);
        Product product = MockFactory.buildProduct();

        // when
        when(productService.getProducts(any(Pageable.class))).thenReturn(productsPage);

        // then
        mockMvc.perform(
                        get("/api/products")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "id,asc")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(product.getId()))
                .andExpect(jsonPath("$.content[0].name").value(product.getName()))
                .andExpect(jsonPath("$.content[0].price").value(product.getPrice()))
                .andExpect(jsonPath("$.content[0].registrationDate").value("2026-01-01T01:01:01"))
                .andExpect(jsonPath("$.content[0].modificationDate").value("2026-01-01T01:01:01"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));

        verify(productService, times(1)).getProducts(any(Pageable.class));
    }

    @Test
    void testGetProductById() throws Exception{
        //given
        Long id = 1L;
        ProductDTO responseDTO = MockFactory.buildProductResponseDTO();
        Product product = MockFactory.buildProduct();

        //when
        when(productService.getProductById(id)).thenReturn(responseDTO);

        //then
        mockMvc.perform(
                get("/api/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.price").value(product.getPrice()))
                .andExpect(jsonPath("$.registrationDate").value(product.getRegistrationDate().toString()))
                .andExpect(jsonPath("$.modificationDate").value(product.getModificationDate().toString()));

        verify(productService, times(1)).getProductById(id);
    }

    @Test
    void testCreateProduct() throws Exception {
        //given
        ProductDTO requestDTO = MockFactory.buildProductRequestDTO();
        ProductDTO responseDTO = MockFactory.buildProductResponseDTO();

        //when
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(responseDTO);

        //then
        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(requestDTO))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDTO.getId()))
                .andExpect(jsonPath("$.name").value(responseDTO.getName()))
                .andExpect(jsonPath("$.price").value(responseDTO.getPrice()))
                .andExpect(jsonPath("$.registrationDate").exists())
                .andExpect(jsonPath("$.modificationDate").exists());

        verify(productService).createProduct(any(ProductDTO.class));
    }

    @Test
    void testUpdateProduct() throws Exception{
        //given
        Product product = MockFactory.buildProduct();
        ProductDTO requestDTO = MockFactory.buildProductRequestDTO();
        ProductDTO responseDTO = MockFactory.buildProductResponseDTO();

        //when
        when(productService.updateProduct(eq(product.getId()), any(ProductDTO.class))).thenReturn(responseDTO);

        //then
        mockMvc.perform(
                        put("/api/products/{id}", product.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(requestDTO))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDTO.getId()))
                .andExpect(jsonPath("$.name").value(responseDTO.getName()))
                .andExpect(jsonPath("$.price").value(responseDTO.getPrice()))
                .andExpect(jsonPath("$.registrationDate").exists())
                .andExpect(jsonPath("$.modificationDate").exists());

        verify(productService).updateProduct(anyLong(), any(ProductDTO.class));
    }


    @Test
    void testDeleteProduct() throws Exception {
        //given
        Product product = MockFactory.buildProduct();

        //when
        doNothing().when(productService).deleteProduct(product.getId());

        //then
        mockMvc.perform(
                delete("/api/products/{id}", product.getId())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        verify(productService).deleteProduct(product.getId());
    }


    @Test
    void shouldReturnNotFoundWhenProductIsDeleted() throws Exception {
        //given
        Long id = 1L;

        //when
        when(productService.getProductById(id))
                .thenThrow(new NotFoundException("the product does not exist"));

        //then
        mockMvc.perform(
                        get("/api/products/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("the product does not exist"));

        verify(productService).getProductById(id);
    }



    @Test
    void shouldReturnBadRequestWhenCreateProductWithInvalidArguments() throws Exception {
        //given
        ProductDTO invalidDTO = ProductDTO.builder()
                .name("ag")
                .price(BigDecimal.ZERO)
                .build();

        //when
        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(invalidDTO))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        allOf(
                                containsString("name: The name must be between 3 and 50 letters long"),
                                containsString("price: The price must be a positive value")
                        )));

        //then
        verify(productService, never()).createProduct(any());
    }


    @Test
    void getDeletedProducts() throws Exception {
        // given
        Pageable pageable = MockFactory.buildPageable();
        Page<ProductDTO> productsPage = MockFactory.getProductsDTOPage(pageable);
        Product product = MockFactory.buildProduct();

        // when
        when(productService.getDeletedProducts(any(Pageable.class))).thenReturn(productsPage);

        // then
        mockMvc.perform(
                        get("/api/products/deleted")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "id,asc")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(product.getId()))
                .andExpect(jsonPath("$.content[0].name").value(product.getName()))
                .andExpect(jsonPath("$.content[0].price").value(product.getPrice()))
                .andExpect(jsonPath("$.content[0].registrationDate").value("2026-01-01T01:01:01"))
                .andExpect(jsonPath("$.content[0].modificationDate").value("2026-01-01T01:01:01"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));

        verify(productService, times(1)).getDeletedProducts(any(Pageable.class));
    }


    @Test
    void testRestoreProduct() throws Exception {
        //given
        Long id = 1L;
        Product product = MockFactory.buildProduct();
        ProductDTO dto = MockFactory.buildProductResponseDTO();

        //when
        when(productService.restoreProduct(id)).thenReturn(dto);

        //then
        mockMvc.perform(
                patch("/api/products/{id}/restore", id)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.price").value(dto.getPrice()))
                .andExpect(jsonPath("$.registrationDate").exists())
                .andExpect(jsonPath("$.modificationDate").exists());

        verify(productService).restoreProduct(id);
    }
}
