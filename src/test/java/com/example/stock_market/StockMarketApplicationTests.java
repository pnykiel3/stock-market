package com.example.stock_market;

import com.example.stock_market.integration.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfig.class)
class StockMarketApplicationTests {

	@Test
	void contextLoads() {
	}
}
