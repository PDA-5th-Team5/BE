package com.pda.portfolioservice.controller;

import com.pda.portfolioservice.dto.request.SharePortfolioCommentRequestDTO;
import com.pda.portfolioservice.dto.response.*;
import com.pda.portfolioservice.service.PortfolioService;
import com.pda.utilservice.response.ApiResponse;
import com.pda.utilservice.response.code.resultCode.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/test")
    public String test2() {
        return "Portfolio test";
    }

    // 나의 포트폴리오 요약 조회
    @GetMapping("/{myPortfolioId}/summary")
    public ApiResponse<PortfolioSummaryResponseDTO> getPortfolioSummary(@PathVariable Long myPortfolioId) {
        PortfolioSummaryResponseDTO response = portfolioService.getPortfolioSummary(myPortfolioId);
        return ApiResponse.onSuccess(response);
    }

    // 나의 포트폴리오 제목 리스트 조회
    @GetMapping("/my")
    public ApiResponse<MyPortfolioTitleResponseDTO.myPortfolioListDTO> getMyPortfolioTitleList() {
        MyPortfolioTitleResponseDTO.myPortfolioListDTO response = portfolioService.getMyPortfolioTitleList();
        return ApiResponse.onSuccess(response);
    }

    // 나의 포트폴리오 공유
    @PostMapping("/my/{myPortfolioId}")
    public ApiResponse<ShareMyPortfolioResponseDTO> shareMyPortfolio(@PathVariable(value = "myPortfolioId") Long myPortfolioId) {
        ShareMyPortfolioResponseDTO response = portfolioService.shareMyPortfolio(myPortfolioId);
        return ApiResponse.onSuccess(response);
    }

    // 나의 포트폴리오 삭제
    @DeleteMapping("/my/{myPortfolioId}")
    public ApiResponse<SuccessStatus> deleteMyPortfolio(@PathVariable(value = "myPortfolioId") Long myPortfolioId) {
        portfolioService.deleteMyPortfolio(myPortfolioId);
        return ApiResponse.onSuccess(SuccessStatus.OK);
    }

    // 공유 포트폴리오 리스트 조회
    @GetMapping("/share")
    public ApiResponse<SharePortfolioListResponseDTO> getSharePortfolios(
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "24") int size) {

        SharePortfolioListResponseDTO response = portfolioService.getSharePortfolios(sort, page, size);
        return ApiResponse.onSuccess(response);
    }

    // 공유 포트폴리오 요약 조회
    @GetMapping("/share/{sharePortfolioId}/summary")
    public ApiResponse<SharePortfolioSummaryResponseDTO> getSharePortfolioSummary(@PathVariable Long sharePortfolioId) {
        SharePortfolioSummaryResponseDTO response = portfolioService.getSharePortfolioSummary(sharePortfolioId);

        return ApiResponse.onSuccess(response);
    }

    // 공유 포트폴리오 댓글 작성
    @PostMapping("/share/{sharePortfolioId}/comments")
    public ApiResponse<SuccessStatus> addComment(@PathVariable(value = "sharePortfolioId") Long sharePortfolioId, @RequestBody SharePortfolioCommentRequestDTO requestDTO) {
        portfolioService.addComment(sharePortfolioId, requestDTO);
        return ApiResponse.onSuccess(SuccessStatus.OK);
    }

    // 공유 포트폴리오 댓글 조회
    @GetMapping("/share/{sharePortfolioId}/comments")
    public ApiResponse<SharePortfolioCommentResponseDTO> getComments(@PathVariable(value = "sharePortfolioId") Long sharePortfolioId) {
        SharePortfolioCommentResponseDTO response = portfolioService.getComments(sharePortfolioId);
        return ApiResponse.onSuccess(response);
    }

    // 공유 포트폴리오 댓글 수정
    @PatchMapping("/share/{sharePortfolioId}/comments/{commentId}")
    public ApiResponse<SuccessStatus> updateComment(@PathVariable(value = "sharePortfolioId") Long sharePortfolioId, @PathVariable(value = "commentId") Long commentId, @RequestBody SharePortfolioCommentRequestDTO request) {
        portfolioService.updateComment(sharePortfolioId, commentId, request);
        return ApiResponse.onSuccess(SuccessStatus.OK);
    }

    // 공유 포트폴리오 댓글 삭제
    @DeleteMapping("/share/{sharePortfolioId}/comments/{commentId}")
    public ApiResponse<SuccessStatus> deleteComment(@PathVariable(value = "sharePortfolioId") Long sharePortfolioId, @PathVariable(value = "commentId") Long commentId) {
        portfolioService.deleteComment(sharePortfolioId, commentId);
        return ApiResponse.onSuccess(SuccessStatus.OK);
    }
}
