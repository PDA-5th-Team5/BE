package com.pda.stockservice;

import com.pda.stockservice.dto.request.StockFilter;
import com.pda.stockservice.dto.response.StockResponseDTO;
import com.pda.stockservice.entity.Stock;
import com.pda.stockservice.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = "eureka.client.enabled=false")
class StockServiceApplicationTests {
	@Autowired
	private StockService stockService;

	@Test
	public void testSearchStockStatIds() {
		// Given (테스트 데이터 삽입)
		String market = "KOSPI";
		List<String> sectors = List.of("자동차");

		// StockFilter 객체 생성 및 필터 값 설정
		StockFilter stockFilter = new StockFilter();

		StockFilter.Range bsopPrtiRange = new StockFilter.Range();
		bsopPrtiRange.setMin(0.0);
		bsopPrtiRange.setMax(20.0);
		stockFilter.setBsopPrti(bsopPrtiRange);

		StockFilter.Range bpsRange = new StockFilter.Range();
		bpsRange.setMin(0.0);
		bpsRange.setMax(20.0);
		stockFilter.setBps(bpsRange);

		StockFilter.Range perRange = new StockFilter.Range();
		perRange.setMin(0.0);
		perRange.setMax(20.0);
		stockFilter.setPer(perRange);

		// When (서비스 호출)
		List<StockResponseDTO> stockIds = stockService.searchStockInfos(market, sectors, stockFilter);

		System.out.println(stockIds);

		// Then (검증)
		assertNotNull(stockIds);
		assertFalse(stockIds.isEmpty());
	}
}