package com.pda.portfolioservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pda.portfolioservice.model.Portfolio;
import com.pda.portfolioservice.model.Range;
import lombok.Getter;

import java.util.List;

@Getter
public class PortfolioResponseDTO {
    private String category;
    private Long portfolioId;
    private String market;
    private List<String> sector;
    private Range marketCap;
    private Range per;
    private Range eps;
    private Range bps;
    private Range pbr;
    private Range dividendYield;
    private Range foreignerRatio;
    private Range sps;
    private Range saleAccount;
    private Range crntRate;
    private Range lbltRate;
    private Range ntinInrt;
    private Range bsopPrfiInrt;
    private Range grs;
    private Range roeVal;
    private Range bsopPrti;
    private Range thtrNtin;

    public PortfolioResponseDTO fromEntity(Portfolio portfolio) {
        PortfolioResponseDTO responseDTO = new PortfolioResponseDTO();
        responseDTO.category = getCategory();
        responseDTO.portfolioId = getPortfolioId();
        responseDTO.market = getMarket();
        responseDTO.marketCap = getMarketCap();
        responseDTO.sector = getSector();
        responseDTO.per = getPer();
        responseDTO.eps = getEps();
        responseDTO.bps = getBps();
        responseDTO.pbr = getPbr();
        responseDTO.dividendYield = getDividendYield();
        responseDTO.foreignerRatio = getForeignerRatio();
        responseDTO.sps = getSps();
        responseDTO.saleAccount = getSaleAccount();
        responseDTO.crntRate = getCrntRate();
        responseDTO.lbltRate = getLbltRate();
        responseDTO.ntinInrt = getNtinInrt();
        responseDTO.bsopPrfiInrt = getBsopPrfiInrt();
        responseDTO.grs = getGrs();
        responseDTO.roeVal = getRoeVal();
        responseDTO.bsopPrti = getBsopPrti();
        responseDTO.thtrNtin = getThtrNtin();
        return responseDTO;
    }
}