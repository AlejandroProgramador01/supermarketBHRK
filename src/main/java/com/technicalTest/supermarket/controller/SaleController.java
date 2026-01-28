package com.technicalTest.supermarket.controller;

import com.technicalTest.supermarket.dto.BranchDTO;
import com.technicalTest.supermarket.dto.SaleDTO;
import com.technicalTest.supermarket.entity.Sale;
import com.technicalTest.supermarket.exception.NotFoundException;
import com.technicalTest.supermarket.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService service;

    @GetMapping()
    public ResponseEntity<List<SaleDTO>> getSales(){
        return ResponseEntity.ok(service.getSales());
    }

    @PostMapping
    public ResponseEntity<SaleDTO> create(@RequestBody SaleDTO dto){
        SaleDTO created = service.createSale(dto);
        URI location =  ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleDTO> update(@PathVariable Long id, @RequestBody SaleDTO dto) {
        SaleDTO updated = service.updateSale(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteSale(id);
        return ResponseEntity.noContent().build();
    }
}
