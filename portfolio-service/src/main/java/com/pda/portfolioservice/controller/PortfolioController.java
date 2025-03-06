package com.pda.portfolioservice.controller;

import com.pda.portfolioservice.dto.request.PortfolioRequestDTO;
import com.pda.portfolioservice.dto.request.SharePortfolioCommentRequestDTO;
import com.pda.portfolioservice.dto.response.*;
import com.pda.portfolioservice.model.Portfolio;
import com.pda.portfolioservice.service.PortfolioService;
import com.pda.utilservice.jwt.JWTUtil;
import com.pda.utilservice.response.ApiResponse;
import com.pda.utilservice.response.code.resultCode.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final Environment environment;

    @GetMapping("/test")
    public String test2() {
        return "Portfolio test";
    }


    // 포트폴리오 저장 (POST)
    @PostMapping("/my/save")
    public ApiResponse<Void> saveMyPortfolio(@RequestBody PortfolioRequestDTO requestDTO, @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ApiResponse.onFailure(401, "인증 토큰이 필요합니다.");
        }

        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
        String userId = jwtUtil.getBearerUserId(token);
        try {
            portfolioService.saveMyPortfolio(requestDTO.toEntity(), userId);
            return ApiResponse.onSuccess(201, "나의 포트폴리오 저장 완료");
        } catch (IllegalStateException e) {
            return ApiResponse.onFailure(409, e.getMessage());  // 중복 포트폴리오 예외 처리
        }
    }

    // 나의 포트폴리오 조회 (GET)
    @GetMapping("/my/{portfolioId}")
    public ApiResponse<Portfolio> getMyPortfolio(
            @PathVariable(value = "portfolioId") Long portfolioId
    ) {
        String category = "my";
        Portfolio portfolio = portfolioService.getPortfolio(category, portfolioId);
        return ApiResponse.onSuccess(portfolio);
    }

    // 나의 포트폴리오 종목 리스트 조회  (GET)
    @GetMapping("/my/{portfolioId}/stock")
    public ApiResponse<List<StockResponseDTO>> getMyPortfolioStock(
            @PathVariable(value = "portfolioId") Long portfolioId,
            @RequestParam(defaultValue = "0") int page
    ) {
        String category = "my";
        // 포트폴리오 id로 조건 찾기
        Portfolio portfolio = portfolioService.getPortfolio(category, portfolioId);
        // openfeign stock filter에 조건을 보내 포함 종목 가져오기
        List<StockResponseDTO> stocks = portfolioService.getPortfolioStock(portfolio,page);

        return ApiResponse.onSuccess(stocks);
    }


    // 공유 포트폴리오 조회 (GET)
    @GetMapping("/share/{portfolioId}")
    public ApiResponse<Portfolio> getSharePortfolio(
            @PathVariable(value = "portfolioId") Long portfolioId
    ) {
        String category = "share";
        Portfolio portfolio = portfolioService.getPortfolio(category, portfolioId);
        return ApiResponse.onSuccess(portfolio);
    }

    // 공유 포트폴리오 종목 리스트 조회  (GET)
    @GetMapping("/share/{portfolioId}/stock")
    public ApiResponse<List<StockResponseDTO>> getSharePortfolioStock(
            @PathVariable(value = "portfolioId") Long portfolioId,
            @RequestParam(defaultValue = "0") int page
    ) {
        String category = "share";
        // 포트폴리오 id로 조건 찾기
        Portfolio portfolio = portfolioService.getPortfolio(category, portfolioId);
        // openfeign stock filter에 조건을 보내 포함 종목 가져오기
        List<StockResponseDTO> stocks = portfolioService.getPortfolioStock(portfolio,page);

        return ApiResponse.onSuccess(stocks);
    }



    // 포트폴리오 삭제 (DELETE)
    @DeleteMapping("/{category}/{portfolioId}")
    public ApiResponse<SuccessStatus> deletePortfolio(
            @PathVariable(value = "category") String category,
            @PathVariable(value = "portfolioId") Long portfolioId
    ) {
        portfolioService.deletePortfolio(category, portfolioId);
        return ApiResponse.onSuccess(SuccessStatus.OK);
    }

    // 나의 포트폴리오 제목 리스트 조회 (GET)
    @GetMapping("/my")
    public ApiResponse<MyPortfolioTitleResponseDTO.myPortfolioListDTO> getMyPortfolioTitleList(
            @RequestHeader(value = "Authorization", required = false) String token) {
        MyPortfolioTitleResponseDTO.myPortfolioListDTO response = portfolioService.getMyPortfolioTitleList(token);
        return ApiResponse.onSuccess(response);
    }

    // 나의 포트폴리오 공유 (POST)
    @PostMapping("/my/{myPortfolioId}")
    public ApiResponse<ShareMyPortfolioResponseDTO> shareMyPortfolio(
            @PathVariable(value = "myPortfolioId") Long myPortfolioId,
            @RequestHeader(value = "Authorization", required = false) String token)
    {
        ShareMyPortfolioResponseDTO response = portfolioService.shareMyPortfolio(myPortfolioId, token);
        return ApiResponse.onSuccess(response);
    }

    // 나의 포트폴리오 삭제 (DELETE)
    @DeleteMapping("/my/{myPortfolioId}")
    public ApiResponse<SuccessStatus> deleteMyPortfolio(
            @PathVariable Long myPortfolioId,
            @RequestHeader(value = "Authorization", required = false) String token)
    {
        portfolioService.deleteMyPortfolio(myPortfolioId, token);
        return ApiResponse.onSuccess(SuccessStatus.OK);
    }


    // 공유 포트폴리오 댓글 작성 (POST)
    @PostMapping("/share/{sharePortfolioId}/comments")
    public ApiResponse<SuccessStatus> addComment(@PathVariable(value = "sharePortfolioId") Long sharePortfolioId, @RequestBody SharePortfolioCommentRequestDTO requestDTO, @RequestHeader(value = "Authorization", required = false) String token) {
        portfolioService.addComment(sharePortfolioId, requestDTO, token);
        return ApiResponse.onSuccess(SuccessStatus.OK);
    }

    // 공유 포트폴리오 댓글 조회 (GET)
    @GetMapping("/share/{sharePortfolioId}/comments")
    public ApiResponse<SharePortfolioCommentResponseDTO> getComments(@PathVariable(value = "sharePortfolioId") Long sharePortfolioId) {
        SharePortfolioCommentResponseDTO response = portfolioService.getComments(sharePortfolioId);
        return ApiResponse.onSuccess(response);
    }

    // 공유 포트폴리오 댓글 수정 (PATCH)
    @PatchMapping("/share/{sharePortfolioId}/comments/{commentId}")
    public ApiResponse<SuccessStatus> updateComment(@PathVariable(value = "sharePortfolioId") Long sharePortfolioId, @PathVariable(value = "commentId") Long commentId,
                                                    @RequestBody SharePortfolioCommentRequestDTO request, @RequestHeader(value = "Authorization", required = false) String token) {
        portfolioService.updateComment(sharePortfolioId, commentId, request, token);
        return ApiResponse.onSuccess(SuccessStatus.OK);
    }

    // 공유 포트폴리오 댓글 삭제 (DELETE)
    @DeleteMapping("/share/{sharePortfolioId}/comments/{commentId}")
    public ApiResponse<SuccessStatus> deleteComment(@PathVariable(value = "sharePortfolioId") Long sharePortfolioId, @PathVariable(value = "commentId") Long commentId,
                                                    @RequestHeader(value = "Authorization", required = false) String token) {
        portfolioService.deleteComment(sharePortfolioId, commentId, token);
        return ApiResponse.onSuccess(SuccessStatus.OK);
    }

    // userId로 공유 포트폴리오 댓글 조회 (GET)
    @GetMapping("/{userId}/my/comments")
    public MyPortfolioCommentsResponseDTO getNickname(@PathVariable String userId) {
        return portfolioService.getCommentsByUserId(userId);
    }


}
