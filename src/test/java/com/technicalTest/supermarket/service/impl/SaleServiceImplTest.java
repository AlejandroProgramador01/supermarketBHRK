package com.technicalTest.supermarket.service.impl;

import com.technicalTest.supermarket.dto.SaleDTO;
import com.technicalTest.supermarket.dto.SaleDetailResponseDTO;
import com.technicalTest.supermarket.entity.Branch;
import com.technicalTest.supermarket.entity.Product;
import com.technicalTest.supermarket.entity.Sale;
import com.technicalTest.supermarket.exception.NotFoundException;
import com.technicalTest.supermarket.exception.SaleQuantityIncreaseNotAllowedException;
import com.technicalTest.supermarket.mockFactory.MockFactory;
import com.technicalTest.supermarket.repository.BranchRepository;
import com.technicalTest.supermarket.repository.ProductRepository;
import com.technicalTest.supermarket.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private BranchRepository branchRepository;
    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private SaleServiceImpl saleServiceImpl;

    @Test
    void shouldGetSales() {
        //given
        List<Sale> mockSales = MockFactory.getSales();
        var saleEntity = mockSales.get(0);

        //when
        when(saleRepository.findAll()).thenReturn(mockSales);

        //then
        List<SaleDTO> sale = saleServiceImpl.getSales();
        var firstSale = sale.get(0);
        assertThat(firstSale)
                .isNotNull()
                .hasNoNullFieldsOrPropertiesExcept("detailsRequest")
                .extracting(
                        SaleDTO::getId,
                        SaleDTO::getDate,
                        SaleDTO::getStatus,
                        SaleDTO::getTotal,
                        SaleDTO::getIdBranch
                ).containsExactly(
                        saleEntity.getId(),
                        saleEntity.getDate(),
                        saleEntity.getStatus(),
                        saleEntity.getTotal(),
                        saleEntity.getBranch().getId()
                );
        assertThat(firstSale)
                .extracting(
                        SaleDTO::getDetailsResponse
                ).isNotNull();

        var saleEntityDetails = saleEntity.getDetails();
        var saleDetailEntity = saleEntityDetails.get(0);

        var saleDetailsDto = sale.get(0).getDetailsResponse();
        var saleDetailDto = saleDetailsDto.get(0);

        assertThat(saleDetailDto)
                .extracting(
                        SaleDetailResponseDTO::getId,
                        SaleDetailResponseDTO::getIdProd,
                        SaleDetailResponseDTO::getNameProd,
                        SaleDetailResponseDTO::getQuantityProd,
                        SaleDetailResponseDTO::getPriceProd,
                        SaleDetailResponseDTO::getSubtotal
                ).containsExactly(
                        saleDetailEntity.getId(),
                        saleDetailDto.getIdProd(),
                        saleDetailDto.getNameProd(),
                        saleDetailDto.getQuantityProd(),
                        saleDetailDto.getPriceProd(),
                        saleDetailDto.getSubtotal()
                );

        verify(saleRepository).findAll();
    }


    @Test
    void shouldCreateSale() {
        // given
        SaleDTO mockDto = MockFactory.buildSaleRequestDto();
        Branch mockBranch = MockFactory.buildBranchEntity();
        Product mockProduct = MockFactory.buildProductEntity();

        // when
        when(branchRepository.findById(1L))
                .thenReturn(Optional.of(mockBranch));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(mockProduct));

        when(saleRepository.save(any(Sale.class)))
                .thenAnswer(invocation -> {
                    Sale sale = invocation.getArgument(0);
                    sale.setId(1L);
                    return sale;
                });
        SaleDTO sale = saleServiceImpl.createSale(mockDto);

        // then
        assertThat(sale).isNotNull();
        assertThat(sale.getId()).isEqualTo(1L);
        assertThat(sale.getStatus()).isEqualTo("registrada");
        assertThat(sale.getIdBranch()).isEqualTo(1L);
        verify(branchRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(saleRepository).save(any(Sale.class));
    }

    @Test
    void shouldThrowErrorWhenBranchIsNotFoundForCreateSale() {
        // given
        SaleDTO mockDto = MockFactory.buildSaleRequestDto();

        // when
        when(branchRepository.findById(mockDto.getIdBranch()))
                .thenReturn(Optional.empty());

        // then
        var error = assertThrows(
                NotFoundException.class,
                () -> saleServiceImpl.createSale(mockDto)
        );

        assertEquals("La sucursal no existe.", error.getMessage());
        verify(branchRepository).findById(mockDto.getIdBranch());
        verify(productRepository, times(0)).findById(anyLong());
        verify(saleRepository, times(0)).save(any(Sale.class));
    }

    @Test
    void shouldThrowErrorWhenProductIsNotFoundForCreateSale() {
        // given
        SaleDTO mockDto = MockFactory.buildSaleRequestDto();
        Branch mockBranch = MockFactory.buildBranchEntity();

        // when
        when(branchRepository.findById(mockDto.getIdBranch()))
                .thenReturn(Optional.of(mockBranch));

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        // then
        var error = assertThrows(
                NotFoundException.class,
                () -> saleServiceImpl.createSale(mockDto)
        );
        assertEquals("Producto no existe.", error.getMessage());
        verify(branchRepository).findById(mockDto.getIdBranch());
        verify(productRepository).findById(1L);
        verify(saleRepository, times(0)).save(any(Sale.class));
    }



    @Test
    void shouldUpdateSale() {
        // given
        Long saleId = 1L;

        Sale existingSale = MockFactory.buildSaleEntity();
        SaleDTO mockDto = MockFactory.buildSaleRequestDto();

        mockDto.getDetailsRequest().get(0).setQuantityProd(5);

        when(saleRepository.findById(saleId))
                .thenReturn(Optional.of(existingSale));

        when(saleRepository.save(any(Sale.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        SaleDTO saleDTO = saleServiceImpl.updateSale(saleId, mockDto);

        // then
        assertThat(saleDTO).isNotNull();
        assertThat(saleDTO.getId()).isEqualTo(saleId);
        assertThat(saleDTO.getStatus()).isEqualTo("modificada");
        verify(saleRepository).findById(saleId);
        verify(saleRepository).save(existingSale);
    }


    @Test
    void shouldThrowErrorWhenSaleIsNotFoundForUpdate() {
        // given
        Long saleId = 1L;
        SaleDTO mockDto = MockFactory.buildSaleRequestDto();

        // when
        when(saleRepository.findById(saleId))
                .thenReturn(Optional.empty());

        // then
        var error = assertThrows(
                NotFoundException.class,
                () -> saleServiceImpl.updateSale(saleId, mockDto)
        );

        assertEquals("La venta no existe.", error.getMessage());
        verify(saleRepository).findById(saleId);
        verify(saleRepository, times(0)).save(any(Sale.class));
    }

    @Test
    void shouldThrowErrorWhenProductDoesNotBelongToSaleForUpdate() {
        // given
        Long saleId = 1L;
        Sale existingSale = MockFactory.buildSaleEntity();
        SaleDTO mockDto = MockFactory.buildSaleRequestDto();
        mockDto.getDetailsRequest().get(0).setIdProd(2L);

        //when
        when(saleRepository.findById(saleId))
                .thenReturn(Optional.of(existingSale));

        // then
        var error = assertThrows(
                NotFoundException.class,
                () -> saleServiceImpl.updateSale(saleId, mockDto)
        );

        assertEquals(
                "El producto no pertenece a este detalle de venta",
                error.getMessage()
        );

        verify(saleRepository).findById(saleId);
        verify(saleRepository, times(0)).save(any(Sale.class));
    }

    @Test
    void shouldThrowErrorWhenQuantityIsIncreasedForUpdate() {
        // given
        Long saleId = 1L;
        Sale existingSale = MockFactory.buildSaleEntity();
        SaleDTO mockDto = MockFactory.buildSaleRequestDto();
        mockDto.getDetailsRequest().get(0).setQuantityProd(20);

        //when
        when(saleRepository.findById(saleId))
                .thenReturn(Optional.of(existingSale));

        // then
        var error = assertThrows(
                SaleQuantityIncreaseNotAllowedException.class,
                () -> saleServiceImpl.updateSale(saleId, mockDto)
        );

        assertEquals(
                "No se puede actualizar una venta para aumentar la cantidad de productos. Debe registrarse una nueva venta.",
                error.getMessage()
        );

        verify(saleRepository).findById(saleId);
        verify(saleRepository, times(0)).save(any(Sale.class));
    }



    @Test
    void shouldDeleteSale(){
        // given
        Long id = 1L;

        // when
        saleServiceImpl.deleteSale(id);

        // then
        verify(saleRepository, times(1)).deleteById(id);
    }
}
