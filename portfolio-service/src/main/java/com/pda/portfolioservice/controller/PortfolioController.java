package com.pda.portfolioservice.controller;

import com.pda.portfolioservice.dto.request.SharePortfolioCommentRequestDTO;
import com.pda.portfolioservice.dto.response.MyPortfolioTitleResponseDTO;
import com.pda.portfolioservice.dto.response.ShareMyPortfolioResponseDTO;
import com.pda.portfolioservice.dto.response.SharePortfolioCommentResponseDTO;
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


    @GetMapping("/my")
    public ApiResponse<MyPortfolioTitleResponseDTO.myPortfolioListDTO> getMyPortfolioTitleList(@PathVariable Long myPortfolioId) {
        MyPortfolioTitleResponseDTO.myPortfolioListDTO response = portfolioService.getMyPortfolioTitleList(myPortfolioId);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/my/{myPortfolioId}")
    public ApiResponse<ShareMyPortfolioResponseDTO> shareMyPortfolio(@PathVariable Long myPortfolioId) {
        ShareMyPortfolioResponseDTO response = portfolioService.shareMyPortfolio(myPortfolioId);
        return ApiResponse.onSuccess(response);
    }

    @DeleteMapping("/my/{myPortfolioId}")
    public ApiResponse<SuccessStatus> deleteMyPortfolio(@PathVariable Long myPortfolioId) {
        portfolioService.deleteMyPortfolio(myPortfolioId);
        return ApiResponse.onSuccess(SuccessStatus.OK);
    }


    @PostMapping("/share/{sharePortfolioId}/comments")
    public ApiResponse<SuccessStatus> addComment(@PathVariable Long sharePortfolioId, @RequestBody SharePortfolioCommentRequestDTO requestDTO) {
        portfolioService.addComment(sharePortfolioId, requestDTO);
        return ApiResponse.onSuccess(SuccessStatus.OK);
    }

    @GetMapping("/share/{sharePortfolioId}/comments")
    public ApiResponse<SharePortfolioCommentResponseDTO> getComments(@PathVariable Long sharePortfolioId) {
        SharePortfolioCommentResponseDTO response = portfolioService.getComments(sharePortfolioId);
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/share/{sharePortfolioId}/comments/{commentId}")
    public ApiResponse<SuccessStatus> updateComment(Long sharePortfolioId, Long commentId, @RequestBody SharePortfolioCommentRequestDTO request) {
        portfolioService.updateComment(sharePortfolioId, commentId, request);
        return ApiResponse.onSuccess(SuccessStatus.OK);
    }

    @DeleteMapping("/share/{sharePortfolioId}/comments/{commentId}")
    public ApiResponse<SuccessStatus> deleteComment(Long sharePortfolioId, Long commentId) {
        portfolioService.deleteComment(sharePortfolioId, commentId);
        return ApiResponse.onSuccess(SuccessStatus.OK);
    }
}
