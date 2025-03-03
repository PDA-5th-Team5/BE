package com.pda.portfolioservice.service;

import com.pda.portfolioservice.dto.request.SharePortfolioCommentRequestDTO;
import com.pda.portfolioservice.dto.response.MyPortfolioTitleResponseDTO;
import com.pda.portfolioservice.dto.response.ShareMyPortfolioResponseDTO;
import com.pda.portfolioservice.dto.response.SharePortfolioCommentResponseDTO;

public interface PortfolioService {

    // 나의 포트폴리오 제목 리스트 조회
    public MyPortfolioTitleResponseDTO.myPortfolioListDTO getMyPortfolioTitleList(Long myPortfolioId);

    // 나의 포트폴리오 공유
    public ShareMyPortfolioResponseDTO shareMyPortfolio(Long sharePortfolioId);

    // 나의 포트폴리오 삭제
    public void deleteMyPortfolio(Long myPortfolioId);

    // 공유 포트폴리오 댓글 작성
    public void addComment(Long sharePortfolioId, SharePortfolioCommentRequestDTO requestDTO);

    // 공유 포트폴리오 댓글 조회
    public SharePortfolioCommentResponseDTO getComments(Long sharePortfolioId);

    // 공유 포트폴리오 댓글 수정
    public void updateComment(Long sharePortfolioId, Long commentId, SharePortfolioCommentRequestDTO requestDTO);

    // 공유 포트폴리오 댓글 삭제
    public void deleteComment(Long sharePortfolioId, Long commentId);
}
