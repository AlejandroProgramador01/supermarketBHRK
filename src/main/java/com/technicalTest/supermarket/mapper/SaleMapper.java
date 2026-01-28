package com.technicalTest.supermarket.mapper;

import com.technicalTest.supermarket.dto.SaleDTO;
import com.technicalTest.supermarket.dto.SaleDetailResponseDTO;
import com.technicalTest.supermarket.entity.Branch;
import com.technicalTest.supermarket.entity.Product;
import com.technicalTest.supermarket.entity.Sale;
import com.technicalTest.supermarket.entity.SaleDetail;
import com.technicalTest.supermarket.exception.NotFoundException;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    static SaleDTO toDto(Sale sale){
        if (sale == null) return null;

        List<SaleDetailResponseDTO> details = sale.getDetails().stream()
                .map(det -> SaleDetailResponseDTO.builder()
                        .id(det.getId())
                        .idProd(det.getProduct().getId())
                        .nameProd(det.getProduct().getName())
                        .quantityProd(det.getQuantityProd())
                        .priceProd(det.getPriceProd())
                        .subtotal(det.getProduct().getPrice().multiply(BigDecimal.valueOf(det.getQuantityProd())))
                        .build()
                ).toList();

        return SaleDTO.builder()
                .id(sale.getId())
                .date(sale.getDate())
                .idBranch(sale.getBranch().getId())
                .status(sale.getStatus())
                .detailsResponse(details)
                .total(sale.getTotal())
                .build();
    }
}

