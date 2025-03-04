package com.pda.portfolioservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pda.portfolioservice.model.Portfolio;
import lombok.Getter;

import java.util.List;

@Getter
public class PortfolioResponseDTO {
    private String category;
    private Long portfolioId;
    private String market;
    private List<String> sector;
    private Portfolio.Range marketCap;
    private Portfolio.Range per;
    private Portfolio.Range eps;
    private Portfolio.Range bps;
    private Portfolio.Range pbr;
    private Portfolio.Range dividendYield;
    private Portfolio.Range foreignerRatio;
    private Portfolio.Range sps;
    private Portfolio.Range saleAccount;
    private Portfolio.Range crntRate;
    private Portfolio.Range lbltRate;
    private Portfolio.Range ntinInrt;
    private Portfolio.Range bsopPrfiInrt;
    private Portfolio.Range grs;
    private Portfolio.Range roeVal;
    private Portfolio.Range bsopPrti;
    private Portfolio.Range thtrNtin;

    public static PortfolioResponseDTO fromEntity(Portfolio portfolio) {
        PortfolioResponseDTO responseDTO = new PortfolioResponseDTO();
        responseDTO.category = portfolio.getCategory();
        responseDTO.portfolioId = portfolio.getPortfolioId();
        responseDTO.market = portfolio.getMarket();
        responseDTO.marketCap = portfolio.getMarketCap();
        responseDTO.sector = portfolio.getSector();
        responseDTO.per = portfolio.getPer();
        responseDTO.eps = portfolio.getEps();
        responseDTO.bps = portfolio.getBps();
        responseDTO.pbr = portfolio.getPbr();
        responseDTO.dividendYield = portfolio.getDividendYield();
        responseDTO.foreignerRatio = portfolio.getForeignerRatio();
        responseDTO.sps = portfolio.getSps();
        responseDTO.saleAccount = portfolio.getSaleAccount();
        responseDTO.crntRate = portfolio.getCrntRate();
        responseDTO.lbltRate = portfolio.getLbltRate();
        responseDTO.ntinInrt = portfolio.getNtinInrt();
        responseDTO.bsopPrfiInrt = portfolio.getBsopPrfiInrt();
        responseDTO.grs = portfolio.getGrs();
        responseDTO.roeVal = portfolio.getRoeVal();
        responseDTO.bsopPrti = portfolio.getBsopPrti();
        responseDTO.thtrNtin = portfolio.getThtrNtin();
        return responseDTO;
    }
}