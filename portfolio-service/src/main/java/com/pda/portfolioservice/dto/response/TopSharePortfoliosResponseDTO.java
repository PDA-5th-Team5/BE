package com.pda.portfolioservice.dto.response;

import com.pda.portfolioservice.model.Portfolio;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TopSharePortfoliosResponseDTO {

    private List<PortfolioInfo> topSnowFlake;


}

class PortfolioInfo {
    private int importCnt; // 가져온 데이터 개수
    private String portfolioSub; // 포트폴리오 설명
    private Portfolio snowflakeP; // 세부 정보
}
