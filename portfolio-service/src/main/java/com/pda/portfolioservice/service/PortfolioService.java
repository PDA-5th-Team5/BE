package com.pda.portfolioservice.service;

import com.pda.portfolioservice.dto.request.SharePortfolioCommentRequestDTO;
import com.pda.portfolioservice.dto.response.*;

public interface PortfolioService {

    // 나의 포트폴리오 요약 조회
    public PortfolioSummaryResponseDTO getPortfolioSummary(Long myPortfolioId);

    // 나의 포트폴리오 제목 리스트 조회
    public MyPortfolioTitleResponseDTO.myPortfolioListDTO getMyPortfolioTitleList();

    // 나의 포트폴리오 공유
    public ShareMyPortfolioResponseDTO shareMyPortfolio(Long sharePortfolioId);

    // 나의 포트폴리오 삭제
    public void deleteMyPortfolio(Long myPortfolioId);

    // 공유 포트폴리오 리스트 조회
    public SharePortfolioListResponseDTO getSharePortfolios(String sort, int page, int size);

    // 공유 포트폴리오 가져오기 (저장)
    public ImportSharePortfolioResponseDTO getSharePortfolio(Long sharePortfolioId);

    // 공유 포트폴리오 요약 조회
    public SharePortfolioSummaryResponseDTO getSharePortfolioSummary(Long sharePortfolioId);

    // 공유 포트폴리오 스노우 플래이크 조회
//    public SharePortfolioSnowflakePResponseDTO getSharePortfolioSnowflakeP(Long sharePortfolioId);

    // 공유 포트폴리오 댓글 작성
    public void addComment(Long sharePortfolioId, SharePortfolioCommentRequestDTO requestDTO);

    // 공유 포트폴리오 댓글 조회
    public SharePortfolioCommentResponseDTO getComments(Long sharePortfolioId);

    // 공유 포트폴리오 댓글 수정
    public void updateComment(Long sharePortfolioId, Long commentId, SharePortfolioCommentRequestDTO requestDTO);

    // 공유 포트폴리오 댓글 삭제
    public void deleteComment(Long sharePortfolioId, Long commentId);

}
