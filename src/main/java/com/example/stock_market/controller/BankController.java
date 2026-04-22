package com.example.stock_market.controller;

import com.example.stock_market.dto.BankStateRequest;
import com.example.stock_market.dto.BankStateResponse;
import com.example.stock_market.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stocks")
public class BankController {

    private final BankService bankService;

    @GetMapping
    public ResponseEntity<BankStateResponse> getBankState(){
        return ResponseEntity.ok(new BankStateResponse(bankService.getState()));
    }

    @PostMapping
    public ResponseEntity<Void> setBankState(@RequestBody BankStateRequest bankStateRequest) {
        bankService.setState(bankStateRequest.stocks());
        return ResponseEntity.ok().build();
    }
}
