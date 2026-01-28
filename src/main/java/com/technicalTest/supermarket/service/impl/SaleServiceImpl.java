package com.technicalTest.supermarket.service.impl;

import com.technicalTest.supermarket.dto.SaleDTO;
import com.technicalTest.supermarket.entity.Branch;
import com.technicalTest.supermarket.entity.Product;
import com.technicalTest.supermarket.entity.Sale;
import com.technicalTest.supermarket.entity.SaleDetail;
import com.technicalTest.supermarket.exception.NotFoundException;
import com.technicalTest.supermarket.exception.SaleQuantityIncreaseNotAllowedException;
import com.technicalTest.supermarket.mapper.SaleMapper;
import com.technicalTest.supermarket.repository.BranchRepository;
import com.technicalTest.supermarket.repository.ProductRepository;
import com.technicalTest.supermarket.repository.SaleRepository;
import com.technicalTest.supermarket.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    @Override
    public List<SaleDTO> getSales() {
        return saleRepository.findAll().stream().map(SaleMapper::toDto).toList();
    }

    @Override
    public SaleDTO createSale(SaleDTO saleDTO) {
        Branch branch = branchRepository.findById(saleDTO.getIdBranch())
                .orElseThrow(() -> new NotFoundException("La sucursal no existe."));
        Sale sale = new Sale();
        sale.setDate(LocalDateTime.now());
        sale.setStatus("registrada");
        sale.setBranch(branch);
        List<SaleDetail> details = saleDTO.getDetailsRequest().stream()
                .map(saleDetailRequestDTO -> {
                    Product product = productRepository.findById(saleDetailRequestDTO.getIdProd())
                            .orElseThrow(() -> new NotFoundException("Producto no existe."));
                    SaleDetail detail = new SaleDetail();
                    detail.setPriceProd(product.getPrice());
                    detail.setProduct(product);
                    detail.setQuantityProd(saleDetailRequestDTO.getQuantityProd());
                    detail.setSale(sale);
                    product.setStock(product.getStock()- saleDetailRequestDTO.getQuantityProd());
                    return detail;
                }).toList();
        sale.setDetails(details);
        BigDecimal total = details.stream()
                .map(saleDetail -> saleDetail.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(saleDetail.getQuantityProd())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        sale.setTotal(total);
        return SaleMapper.toDto(saleRepository.save(sale));
    }

    @Override
    public SaleDTO updateSale(Long id, SaleDTO saleDTO) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La venta no existe."));
        sale.setDate(LocalDateTime.now());
        sale.setStatus("modificada");
        saleDTO.getDetailsRequest().forEach(saleDetailRequestDTO->{
            SaleDetail existentDetail = sale.getDetails().stream()
                    .filter(detail->{
                        var existantProductId = detail.getProduct().getId();
                        var productId = saleDetailRequestDTO.getIdProd();
                        return Objects.equals(existantProductId, productId);
                    }).findFirst()
                    .orElseThrow(() -> new NotFoundException("El producto no pertenece a este detalle de venta"));
                if (existentDetail.getQuantityProd()>=saleDetailRequestDTO.getQuantityProd()){
                    existentDetail.getProduct().setStock(existentDetail.getProduct().getStock()
                            + (existentDetail.getQuantityProd() - saleDetailRequestDTO.getQuantityProd()));
                    existentDetail.setQuantityProd(saleDetailRequestDTO.getQuantityProd());
                }else{
                    throw new SaleQuantityIncreaseNotAllowedException();
                }
        });
        BigDecimal total = sale.getDetails().stream()
                .map(detail -> detail.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(detail.getQuantityProd())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        sale.setTotal(total);
        return SaleMapper.toDto(saleRepository.save(sale));
    }

    @Override
    public void deleteSale(Long id) {
        saleRepository.deleteById(id);
    }
}
