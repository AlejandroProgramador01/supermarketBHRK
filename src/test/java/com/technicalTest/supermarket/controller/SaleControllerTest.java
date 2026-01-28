package com.technicalTest.supermarket.controller;

import com.technicalTest.supermarket.dto.ProductDTO;
import com.technicalTest.supermarket.dto.SaleDTO;
import com.technicalTest.supermarket.entity.Product;
import com.technicalTest.supermarket.entity.Sale;
import com.technicalTest.supermarket.handler.GlobalExceptionHandler;
import com.technicalTest.supermarket.mockFactory.MockFactory;
import com.technicalTest.supermarket.service.ProductService;
import com.technicalTest.supermarket.service.SaleService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SaleControllerTest {

    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private SaleService saleService;
    @InjectMocks
    private SaleController controller;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
    }

    @Test
    void getSales() throws Exception{
        //given
        List<SaleDTO> sales = MockFactory.getSalesDTO();

        //when
        when(saleService.getSales()).thenReturn(sales);

        //then
        mockMvc.perform(
                        get("/api/sales")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(sales))
                ).andExpect(status().isOk())

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("registrada"))
                .andExpect(jsonPath("$[0].total").value(10000))
                .andExpect(jsonPath("$[0].idBranch").value(1))

                .andExpect(jsonPath("$[0].detailsResponse[0].id").value(1))
                .andExpect(jsonPath("$[0].detailsResponse[0].idProd").value(1))
                .andExpect(jsonPath("$[0].detailsResponse[0].nameProd").value("agua"))
                .andExpect(jsonPath("$[0].detailsResponse[0].quantityProd").value(10))
                .andExpect(jsonPath("$[0].detailsResponse[0].priceProd").value(1000))
                .andExpect(jsonPath("$[0].detailsResponse[0].subtotal").value(10000));

        verify(saleService).getSales();
    }

    @Test
    void testCreateSale() throws Exception {
        //given
        SaleDTO saleDTORequest = MockFactory.buildSaleRequestDto();
        SaleDTO saleDTOResponse = MockFactory.buildSaleResponseDto();

        //when
        when(saleService.createSale(any(SaleDTO.class))).thenReturn(saleDTOResponse);

        //then
        mockMvc.perform(
                        post("/api/sales")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(saleDTORequest))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("registrada"))
                .andExpect(jsonPath("$.total").value(10000))
                .andExpect(jsonPath("$.idBranch").value(1))
                .andExpect(jsonPath("$.detailsResponse[0].id").value(1))
                .andExpect(jsonPath("$.detailsResponse[0].idProd").value(1))
                .andExpect(jsonPath("$.detailsResponse[0].nameProd").value("agua"))
                .andExpect(jsonPath("$.detailsResponse[0].quantityProd").value(10))
                .andExpect(jsonPath("$.detailsResponse[0].priceProd").value(1000))
                .andExpect(jsonPath("$.detailsResponse[0].subtotal").value(10000));

        verify(saleService).createSale(any(SaleDTO.class));
    }

    @Test
    void testUpdateSale() throws Exception{
        //given
        Sale product = MockFactory.buildSaleEntity();
        SaleDTO saleDTORequest = MockFactory.buildSaleRequestDto();
        SaleDTO saleDTOResponse = MockFactory.buildSaleResponseDto();

        //when
        when(saleService.updateSale(eq(1L), any(SaleDTO.class))).thenReturn(saleDTOResponse);

        //then
        mockMvc.perform(
                        put("/api/sales/{id}", product.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(saleDTORequest))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("registrada"))
                .andExpect(jsonPath("$.total").value(10000))
                .andExpect(jsonPath("$.idBranch").value(1))
                .andExpect(jsonPath("$.detailsResponse[0].id").value(1))
                .andExpect(jsonPath("$.detailsResponse[0].idProd").value(1))
                .andExpect(jsonPath("$.detailsResponse[0].nameProd").value("agua"))
                .andExpect(jsonPath("$.detailsResponse[0].quantityProd").value(10))
                .andExpect(jsonPath("$.detailsResponse[0].priceProd").value(1000))
                .andExpect(jsonPath("$.detailsResponse[0].subtotal").value(10000));

        verify(saleService).updateSale(anyLong(), any(SaleDTO.class));
    }

    @Test
    void testDeleteSale() throws Exception {
        //given
        Sale sale = MockFactory.buildSaleEntity();

        //when
        doNothing().when(saleService).deleteSale(sale.getId());

        //then
        mockMvc.perform(
                delete("/api/sales/{id}", sale.getId())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        verify(saleService).deleteSale(sale.getId());
    }

}
