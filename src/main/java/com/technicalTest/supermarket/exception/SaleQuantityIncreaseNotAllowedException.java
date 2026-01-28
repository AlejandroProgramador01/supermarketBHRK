package com.technicalTest.supermarket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SaleQuantityIncreaseNotAllowedException extends RuntimeException {
    public SaleQuantityIncreaseNotAllowedException() {
        super("No se puede actualizar una venta para aumentar la cantidad de productos. Debe registrarse una nueva venta.");
    }
}
