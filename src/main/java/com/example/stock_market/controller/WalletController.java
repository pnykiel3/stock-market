package com.example.stock_market.controller;

import com.example.stock_market.dto.TradeRequest;
import com.example.stock_market.dto.WalletResponse;
import com.example.stock_market.model.TransactionType;
import com.example.stock_market.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{wallet_id}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable("wallet_id") String walletId){
        return ResponseEntity.ok(walletService.getWallet(walletId));
    }

    @GetMapping("/{wallet_id}/stocks/{stock_name}")
    public ResponseEntity<Integer> getStockQuantity(@PathVariable("wallet_id") String walletId, @PathVariable("stock_name") String stockName) {
        return ResponseEntity.ok(walletService.getStockQuantity(walletId, stockName));
    }

    @PostMapping("/{wallet_id}/stocks/{stock_name}")
    public ResponseEntity<Void> trade(@PathVariable("wallet_id") String walletId,
                                      @PathVariable("stock_name") String stockName,
                                      @RequestBody TradeRequest request) {
        if( request.type() == TransactionType.BUY ) walletService.buy(walletId, stockName);
        else walletService.sell(walletId, stockName);
        return ResponseEntity.ok().build();

    }


}
