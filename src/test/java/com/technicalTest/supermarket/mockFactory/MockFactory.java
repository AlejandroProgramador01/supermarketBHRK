package com.technicalTest.supermarket.mockFactory;

import com.technicalTest.supermarket.dto.*;
import com.technicalTest.supermarket.entity.Branch;
import com.technicalTest.supermarket.entity.Product;
import com.technicalTest.supermarket.entity.Sale;
import com.technicalTest.supermarket.entity.SaleDetail;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class MockFactory {

    public static BranchDTO buildBranchRequestDto(){
        return BranchDTO.builder()
                .name("sucursal 1")
                .address("mz a casa 1")
                .build();
    }

    public static BranchDTO buildBranchResponseDto() {
        return BranchDTO.builder()
                .id(1L)
                .name("sucursal 1")
                .address("mz a casa 1")
                .build();
    }

    public static Branch buildBranchEntity() {
        return Branch.builder()
                .id(1L)
                .name("sucursal 1")
                .address("mz a casa 1")
                .build();
    }

    public static List<Branch> getBranches(){
        return new ArrayList<>(List.of(MockFactory.buildBranchEntity()));
    }

    public static List<BranchDTO> getBranchesDTO(){
        return new ArrayList<>(List.of(MockFactory.buildBranchResponseDto()));
    }

    //------------------------------------------------------------------------------------------------------------------

    public static ProductDTO buildProductDtoRequestDto(){
        return ProductDTO.builder()
                .id(1L)
                .name("agua")
                .category("bebidas")
                .price(BigDecimal.valueOf(1000))
                .stock(100)
                .build();
    }

    public static ProductDTO buildProductDtoResponseDto(){
        return ProductDTO.builder()
                .id(1L)
                .name("agua")
                .category("bebidas")
                .price(BigDecimal.valueOf(1000))
                .stock(100)
                .build();
    }

    public static Product buildProductEntity() {
        return Product.builder()
                .id(1L)
                .name("agua")
                .category("bebidas")
                .price(BigDecimal.valueOf(1000))
                .stock(100)
                .build();
    }

    public static List<Product> getProducts() {
        return new ArrayList<>(List.of(MockFactory.buildProductEntity()));
    }

    public static List<ProductDTO> getProductsDTO() {
        return new ArrayList<>(List.of(MockFactory.buildProductDtoResponseDto()));
    }

    //------------------------------------------------------------------------------------------------------------------

    public static Sale buildSaleEntity() {

        var detail = SaleDetail.builder()
                .id(1L)
                .priceProd(BigDecimal.valueOf(1000))
                .quantityProd(10)
                .product(MockFactory.buildProductEntity())
                .build();

        Sale sale = Sale.builder()
                .id(1L)
                .date(LocalDateTime.of(2025, Month.DECEMBER, 25, 10, 0))
                .status("registrada")
                .total(BigDecimal.valueOf(10000))
                .branch(buildBranchEntity())
                .details(List.of(detail)).build();

        detail.setSale(sale);
        return sale;
    }

    public static SaleDTO buildSaleRequestDto(){
        return SaleDTO.builder()
                .idBranch(1L)
                .detailsRequest(new ArrayList<>(List.of(buildSaleDetailRequestDto())))
                .build();
    }

    public static SaleDTO buildSaleResponseDto(){
        return SaleDTO.builder()
                .id(1L)
                .status("registrada")
                .total(BigDecimal.valueOf(10000))
                .idBranch(1L)
                .detailsResponse(new ArrayList<>(List.of(buildSaleDetailResponseDto())))
                .build();
    }

    public static SaleDetailRequestDTO buildSaleDetailRequestDto(){
        return SaleDetailRequestDTO.builder()
                .idProd(1L)
                .quantityProd(10)
                .build();
    }

    public static SaleDetailResponseDTO buildSaleDetailResponseDto(){
        return SaleDetailResponseDTO.builder()
                .id(1L)
                .idProd(1L)
                .nameProd("agua")
                .quantityProd(10)
                .priceProd(BigDecimal.valueOf(1000))
                .subtotal(BigDecimal.valueOf(10000))
                .build();
    }


    public static List<Sale> getSales() {
        return List.of(MockFactory.buildSaleEntity());
    }

    public static List<SaleDTO> getSalesDTO() {
        return List.of(MockFactory.buildSaleResponseDto());
    }


}
