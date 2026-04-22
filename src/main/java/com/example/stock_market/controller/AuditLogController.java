package com.example.stock_market.controller;

import com.example.stock_market.dto.AuditLogResponse;
import com.example.stock_market.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<AuditLogResponse> getLog(){
        return ResponseEntity.ok(new AuditLogResponse(auditLogService.getLog().stream().toList()));
    }
}
