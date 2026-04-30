package com.example.stock_market.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.stock_market.repository.AuditLogEntryRepository;
import com.example.stock_market.repository.BankStockRepository;
import com.example.stock_market.repository.WalletStockRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
class StockMarketIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BankStockRepository bankStockRepository;

    @Autowired
    private WalletStockRepository walletStockRepository;

    @Autowired
    private AuditLogEntryRepository auditLogEntryRepository;

    @BeforeEach
    void cleanDatabase() {
        auditLogEntryRepository.deleteAll();
        walletStockRepository.deleteAll();
        bankStockRepository.deleteAll();
    }

// POST /stocks and GET /stocks

    @Test
    void setBankState_andGetIt_shouldWork() throws Exception {
        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stocks\":[{\"name\":\"GOLD\",\"quantity\":100},{\"name\":\"SILVER\",\"quantity\":50}]}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stocks.length()").value(2));
    }

    @Test
    void getBankState_initially_shouldBeEmpty() throws Exception {
        mockMvc.perform(get("/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stocks").isEmpty());
    }

    @Test
    void setBankState_shouldReplaceOldState() throws Exception {
        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stocks\":[{\"name\":\"GOLD\",\"quantity\":100}]}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stocks\":[{\"name\":\"SILVER\",\"quantity\":25}]}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/stocks"))
                .andExpect(jsonPath("$.stocks.length()").value(1))
                .andExpect(jsonPath("$.stocks[0].name").value("SILVER"));
    }

// BUY flow

    @Test
    void buy_shouldTransferStockFromBankToWallet() throws Exception {
        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stocks\":[{\"name\":\"GOLD\",\"quantity\":10}]}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/wallets/w1/stocks/GOLD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"buy\"}"))
                .andExpect(status().isOk());

        // wallet should have 1
        mockMvc.perform(get("/wallets/w1/stocks/GOLD"))
                .andExpect(content().string("1"));

        // bank should have 9
        mockMvc.perform(get("/stocks"))
                .andExpect(jsonPath("$.stocks[0].quantity").value(9));
    }

    @Test
    void buy_whenStockNotExists_shouldReturn404() throws Exception {
        mockMvc.perform(post("/wallets/w1/stocks/BITCOIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"buy\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buy_whenBankHasZeroQuantity_shouldReturn400() throws Exception {
        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stocks\":[{\"name\":\"GOLD\",\"quantity\":1}]}"))
                .andExpect(status().isOk());

        // buy the last one
        mockMvc.perform(post("/wallets/w1/stocks/GOLD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"buy\"}"))
                .andExpect(status().isOk());

        // try again - should fail
        mockMvc.perform(post("/wallets/w1/stocks/GOLD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"buy\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buy_shouldCreateWalletIfNotExists() throws Exception {
        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stocks\":[{\"name\":\"GOLD\",\"quantity\":10}]}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/wallets/newWallet/stocks/GOLD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"buy\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/wallets/newWallet"))
                .andExpect(jsonPath("$.id").value("newWallet"))
                .andExpect(jsonPath("$.stocks[0].name").value("GOLD"))
                .andExpect(jsonPath("$.stocks[0].quantity").value(1));
    }

// SELL flow

    @Test
    void sell_shouldTransferStockFromWalletToBank() throws Exception {
        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stocks\":[{\"name\":\"GOLD\",\"quantity\":10}]}"))
                .andExpect(status().isOk());

        // buy 2
        mockMvc.perform(post("/wallets/w1/stocks/GOLD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"buy\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/wallets/w1/stocks/GOLD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"buy\"}"))
                .andExpect(status().isOk());

        // sell 1
        mockMvc.perform(post("/wallets/w1/stocks/GOLD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"sell\"}"))
                .andExpect(status().isOk());

        // wallet 1
        mockMvc.perform(get("/wallets/w1/stocks/GOLD"))
                .andExpect(content().string("1"));

        // bank 9
        mockMvc.perform(get("/stocks"))
                .andExpect(jsonPath("$.stocks[0].quantity").value(9));
    }

    @Test
    void sell_whenStockNeverExisted_shouldReturn404() throws Exception {
        mockMvc.perform(post("/wallets/w1/stocks/BITCOIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"sell\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void sell_whenWalletHasNoStock_shouldReturn400() throws Exception {
        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stocks\":[{\"name\":\"GOLD\",\"quantity\":10}]}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/wallets/w1/stocks/GOLD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"sell\"}"))
                .andExpect(status().isBadRequest());
    }

// GET /wallets/{id}

    @Test
    void getWallet_whenNotExists_shouldReturnEmptyStocks() throws Exception {
        mockMvc.perform(get("/wallets/nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("nonexistent"))
                .andExpect(jsonPath("$.stocks").isEmpty());
    }

// GET /log

    @Test
    void auditLog_shouldLogOnlySuccessfulOperations() throws Exception {
        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stocks\":[{\"name\":\"GOLD\",\"quantity\":10}]}"))
                .andExpect(status().isOk());

        // successful buy
        mockMvc.perform(post("/wallets/w1/stocks/GOLD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"buy\"}"))
                .andExpect(status().isOk());

        // failed buy (stock not found) - should not be logged
        mockMvc.perform(post("/wallets/w1/stocks/BITCOIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"buy\"}"))
                .andExpect(status().isNotFound());

        // successful sell
        mockMvc.perform(post("/wallets/w1/stocks/GOLD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"sell\"}"))
                .andExpect(status().isOk());

        // should have 2 entries
        mockMvc.perform(get("/log"))
                .andExpect(jsonPath("$.log.length()").value(2))
                .andExpect(jsonPath("$.log[0].type").value("buy"))
                .andExpect(jsonPath("$.log[0].wallet_id").value("w1"))
                .andExpect(jsonPath("$.log[0].stock_name").value("GOLD"))
                .andExpect(jsonPath("$.log[1].type").value("sell"));
    }

    @Test
    void auditLog_shouldNotLogBankOperations() throws Exception {
        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stocks\":[{\"name\":\"GOLD\",\"quantity\":10}]}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/log"))
                .andExpect(jsonPath("$.log").isEmpty());
    }

// full scenario

    @Test
    void fullTradingScenario() throws Exception {
        // set bank: AAPL=5, GOOG=3
        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stocks\":[{\"name\":\"AAPL\",\"quantity\":5},{\"name\":\"GOOG\",\"quantity\":3}]}"))
                .andExpect(status().isOk());

        // trader A buys 2 AAPL
        mockMvc.perform(post("/wallets/traderA/stocks/AAPL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"buy\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/wallets/traderA/stocks/AAPL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"buy\"}"))
                .andExpect(status().isOk());

        // trader B buys 1 GOOG
        mockMvc.perform(post("/wallets/traderB/stocks/GOOG")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"buy\"}"))
                .andExpect(status().isOk());

        // trader A sells 1 AAPL
        mockMvc.perform(post("/wallets/traderA/stocks/AAPL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"sell\"}"))
                .andExpect(status().isOk());

        // traderA has 1 AAPL, traderB has 1 GOOG
        mockMvc.perform(get("/wallets/traderA/stocks/AAPL"))
                .andExpect(content().string("1"));
        mockMvc.perform(get("/wallets/traderB/stocks/GOOG"))
                .andExpect(content().string("1"));

        // should have 4 entries
        mockMvc.perform(get("/log"))
                .andExpect(jsonPath("$.log.length()").value(4));
    }
}
