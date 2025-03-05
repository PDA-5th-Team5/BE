package com.pda.portfolioservice.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SharePortfolioListResponseDTO {

    private int sharePortfoliosCnt;
    private List<SharePortfolioDTO> sharePortfolios;

    @Builder
    @Getter
    public static class SharePortfolioDTO {
        private Long sharePortfolioId;
        private String sharePortfolioTitle;
        private String sharePortfolioDescription;
        private int sharePortfolioImportCnt;
        private SnowflakePDTO snowflakeP;
    }

    @Builder
    @Getter
    public static class SnowflakePDTO {
//        private ElementDTO elements;
        private String market;
        private List<String> sectors;
    }

//    @Builder
//    @Getter
//    public static class ElementDTO {
//        private List<Byte> marketCap;
//        private List<Byte> per;
//        private List<Byte> eps;
//        private List<Byte> bps;
//        private List<Byte> pbr;
//        private List<Byte> dividendYield;
//        private List<Byte> foreignerRatio;
//        private List<Byte> sps;
//        private List<Byte> saleAccount;
//        private List<Byte> crntRate;
//        private List<Byte> lbltRate;
//        private List<Byte> ntinInrt;
//        private List<Byte> bsopPrfiInrt;
//        private List<Byte> grs;
//        private List<Byte> roeVal;
//        private List<Byte> bsopPrti;
//        private List<Byte> thtrNtin;
//    }
}
