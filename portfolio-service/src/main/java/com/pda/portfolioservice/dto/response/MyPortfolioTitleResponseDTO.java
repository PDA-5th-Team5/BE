package com.pda.portfolioservice.dto.response;

import com.pda.portfolioservice.entity.MyPortfolio;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class MyPortfolioTitleResponseDTO {
    @Builder
    @Getter
    public static class myPortfolioDTO {
        private Long myPortfolioId;
        private String myPortfolioTitle;
    }

    @Builder
    @Getter
    public static class myPortfolioListDTO {
        private int myPortfoliosCnt;
        private List<myPortfolioDTO> myPortfolios;

        public static myPortfolioListDTO toDTO(List<MyPortfolio> myPortfolioList) {
            List<myPortfolioDTO> myPortfolioDTOList = myPortfolioList.stream()
                    .map(myPortfolio -> myPortfolioDTO.builder()
                            .myPortfolioId(myPortfolio.getMyPortfolioId())
                            .myPortfolioTitle(myPortfolio.getTitle())
                            .build())
                    .toList();

            return myPortfolioListDTO.builder()
                    .myPortfoliosCnt(myPortfolioDTOList.size())
                    .myPortfolios(myPortfolioDTOList)
                    .build();
        }
    }
}
