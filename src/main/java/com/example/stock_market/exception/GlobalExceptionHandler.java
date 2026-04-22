package com.example.stock_market.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(StockNotFoundException ex) {
        return ResponseEntity.notFound().build();  // 404
    }
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Void> handleBadRequest(InsufficientStockException ex) {
        return ResponseEntity.badRequest().build();  // 400
    }
}