package com.technicalTest.supermarket.dto;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@Builder
public class SaleDetailRequestDTO {
    private Long idProd;
    private Integer quantityProd;
}
