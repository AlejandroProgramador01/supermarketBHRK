package com.technicalTest.supermarket.controller;

import com.technicalTest.supermarket.dto.BranchDTO;
import com.technicalTest.supermarket.dto.ProductDTO;
import com.technicalTest.supermarket.entity.Branch;
import com.technicalTest.supermarket.entity.Product;
import com.technicalTest.supermarket.handler.GlobalExceptionHandler;
import com.technicalTest.supermarket.mockFactory.MockFactory;
import com.technicalTest.supermarket.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

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
                .build();
    }

    @Test
    void getProducts() throws Exception{
        //given
        List<ProductDTO> products = MockFactory.getProductsDTO();

        //when
        when(productService.getProducts()).thenReturn(products);

        //then
        mockMvc.perform(
                        get("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(products))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("agua"))
                .andExpect(jsonPath("$[0].category").value("bebidas"))
                .andExpect(jsonPath("$[0].price").value("1000"))
                .andExpect(jsonPath("$[0].stock").value("100"));

        verify(productService).getProducts();
    }

    @Test
    void testCreateProduct() throws Exception {
        //given
        ProductDTO productDTORequest = MockFactory.buildProductDtoRequestDto();
        ProductDTO productDTOResponse = MockFactory.buildProductDtoResponseDto();

        //when
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(productDTOResponse);

        //then
        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(productDTORequest))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("agua"))
                .andExpect(jsonPath("$.category").value("bebidas"))
                .andExpect(jsonPath("$.price").value("1000"))
                .andExpect(jsonPath("$.stock").value("100"));

        verify(productService).createProduct(any(ProductDTO.class));
    }

    @Test
    void testUpdateProduct() throws Exception{
        //given
        Long id = 1L;
        Product product = MockFactory.buildProductEntity();
        ProductDTO productDTORequest = MockFactory.buildProductDtoRequestDto();
        ProductDTO productDTOResponse = MockFactory.buildProductDtoResponseDto();

        //when
        when(productService.updateProduct(eq(id), any(ProductDTO.class))).thenReturn(productDTOResponse);

        //then
        mockMvc.perform(
                        put("/api/products/{id}", product.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(productDTORequest))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("agua"))
                .andExpect(jsonPath("$.category").value("bebidas"))
                .andExpect(jsonPath("$.price").value("1000"))
                .andExpect(jsonPath("$.stock").value("100"));

        verify(productService).updateProduct(anyLong(), any(ProductDTO.class));
    }

    @Test
    void testDeleteProduct() throws Exception {
        //given
        Product product = MockFactory.buildProductEntity();

        //when
        doNothing().when(productService).deleteProduct(product.getId());

        //then
        mockMvc.perform(
                delete("/api/products/{id}", product.getId())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        verify(productService).deleteProduct(product.getId());
    }

}
