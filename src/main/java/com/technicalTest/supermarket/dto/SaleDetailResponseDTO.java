package com.technicalTest.supermarket.dto;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@Builder
public class SaleDetailResponseDTO {
    private Long id;
    private Long idProd;
    private String nameProd;
    private Integer quantityProd;
    private BigDecimal priceProd;
    private BigDecimal subtotal;
}
