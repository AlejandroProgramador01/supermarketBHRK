package com.technicalTest.supermarket.service;

import com.technicalTest.supermarket.dto.SaleDTO;
import com.technicalTest.supermarket.exception.NotFoundException;

import java.util.List;

public interface SaleService {
    List<SaleDTO> getSales();
    SaleDTO createSale(SaleDTO saleDTO) throws NotFoundException;
    SaleDTO updateSale(Long id, SaleDTO saleDTO);
    void deleteSale(Long id);
}
