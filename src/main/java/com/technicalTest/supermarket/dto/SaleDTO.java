package com.technicalTest.supermarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SaleDTO {
    //sale
    private Long id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalDateTime date;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String status;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal total;

    //branch data
    private Long idBranch;

    //saleDetail data
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<SaleDetailRequestDTO> detailsRequest;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<SaleDetailResponseDTO> detailsResponse;
}
