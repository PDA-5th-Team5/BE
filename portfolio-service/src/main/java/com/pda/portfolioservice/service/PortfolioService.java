package com.pda.portfolioservice.service;

import com.pda.portfolioservice.dto.request.SharePortfolioCommentRequestDTO;
import com.pda.portfolioservice.dto.response.*;
import com.pda.portfolioservice.model.Portfolio;

import java.util.List;

public interface PortfolioService {
    // 포트폴리오 저장
    public Portfolio saveMyPortfolio(Portfolio portfolio, String userId);

    public Portfolio getPortfolio(String category, Long portfolioId);

    //포트폴리오 종목 리스트 조회
    List<StockResponseDTO> getPortfolioStock(Portfolio portfolio, int page);

    void deletePortfolio(String category, Long portfolioId);

    // 나의 포트폴리오 제목 리스트 조회
//    public MyPortfolioTitleResponseDTO.myPortfolioListDTO getMyPortfolioTitleList(Long myPortfolioId,String userId);
    public MyPortfolioTitleResponseDTO.myPortfolioListDTO getMyPortfolioTitleList(String token);

    // 나의 포트폴리오 공유
    public ShareMyPortfolioResponseDTO shareMyPortfolio(Long sharePortfolioId, String token);

    // 나의 포트폴리오 삭제
    public void deleteMyPortfolio(Long myPortfolioId, String token);

    // 공유 포트폴리오 댓글 작성
    public void addComment(Long sharePortfolioId, SharePortfolioCommentRequestDTO requestDTO, String token);

    // 공유 포트폴리오 댓글 조회
    public SharePortfolioCommentResponseDTO getComments(Long sharePortfolioId);

    // 공유 포트폴리오 댓글 수정
    public void updateComment(Long sharePortfolioId, Long commentId, SharePortfolioCommentRequestDTO requestDTO, String token);

    // 공유 포트폴리오 댓글 삭제
    public void deleteComment(Long sharePortfolioId, Long commentId, String token);
//    public void deleteComment(Long sharePortfolioId, Long commentId);

    // userId로 공유 포트폴리오 댓글 조회
    public MyCommentsResponseDTO getCommentsByUserId(String userId);

}
