package com.pda.portfolioservice.controller;

import com.pda.portfolioservice.dto.request.PortfolioMarketGraphRequestDTO;
import com.pda.portfolioservice.dto.request.PortfolioRequestDTO;
import com.pda.portfolioservice.dto.request.SharePortfolioCommentRequestDTO;
import com.pda.portfolioservice.dto.response.*;
import com.pda.portfolioservice.enums.Market;
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
    public ApiResponse<StockSearchResponseDTO> getMyPortfolioStock(
            @PathVariable(value = "portfolioId") Long portfolioId,
            @RequestParam(defaultValue = "0") int page
    ) {
        String category = "my";
        // 포트폴리오 id로 조건 찾기
        Portfolio portfolio = portfolioService.getPortfolio(category, portfolioId);
        // openfeign stock filter에 조건을 보내 포함 종목 가져오기
        StockSearchResponseDTO stocks = portfolioService.getPortfolioStock(portfolio,page);

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
    public ApiResponse<StockSearchResponseDTO> getSharePortfolioStock(
            @PathVariable(value = "portfolioId") Long portfolioId,
            @RequestParam(defaultValue = "0") int page
    ) {
        String category = "share";
        // 포트폴리오 id로 조건 찾기
        Portfolio portfolio = portfolioService.getPortfolio(category, portfolioId);
        // openfeign stock filter에 조건을 보내 포함 종목 가져오기
        StockSearchResponseDTO stocks = portfolioService.getPortfolioStock(portfolio,page);

        return ApiResponse.onSuccess(stocks);
    }

    // 공유 포트폴리오 리스트 조회  (GET)
    @GetMapping("/share/board")
    public ApiResponse<List<SharePortfolioBoardDTO>> getSharePortfolioStockBoard(@RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "createdAt") String sortBy) {
        List<SharePortfolioBoardDTO> spb = portfolioService.getSharePortfolios(page,sortBy);
        return ApiResponse.onSuccess(spb);
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

    @PostMapping("/share/{sharePortfolioId}")
    public ApiResponse<SaveSharePortfolioResponseDTO> saveSharePortfolio(
            @PathVariable(value = "sharePortfolioId") Long sharePortfolioId,
            @RequestHeader(value = "Authorization", required = false) String token)
    {
        SaveSharePortfolioResponseDTO response = portfolioService.saveSharePortfolio(sharePortfolioId, token);
        return ApiResponse.onSuccess(response);
    }
    // 나의 포트폴리오 평균값 조회  (GET)
    @GetMapping("/my/{portfolioId}/summary")
    public ApiResponse<PortfolioSummaryResponseDTO> getMyPortfolioSummary(@PathVariable(value = "portfolioId") Long portfolioId) {
        System.out.println("portfolioId = " + portfolioId);

        String category = "my";
        // 포트폴리오 id로 조건 찾기
        Portfolio portfolio = portfolioService.getPortfolio(category, portfolioId);
        // openfeign stock filter에 조건을 보내 포함 종목의 4가지 요소 평균값 가져오기
        PortfolioSummaryResponseDTO summary = portfolioService.getPortfolioSummary(portfolio);

        return ApiResponse.onSuccess(summary);
    }

    // 공유 포트폴리오 평균값 조회  (GET)
    @GetMapping("/share/{portfolioId}/summary")
    public ApiResponse<PortfolioSummaryResponseDTO> getSharePortfolioSummary(@PathVariable(value = "portfolioId") Long portfolioId) {
        System.out.println("portfolioId = " + portfolioId);

        String category = "share";
        // 포트폴리오 id로 조건 찾기
        Portfolio portfolio = portfolioService.getPortfolio(category, portfolioId);
        // openfeign stock filter에 조건을 보내 포함 종목의 4가지 요소 평균값 가져오기
        PortfolioSummaryResponseDTO summary = portfolioService.getPortfolioSummary(portfolio);

        return ApiResponse.onSuccess(summary);
    }

    // 인기 포트폴리오 조회  (GET)
    @GetMapping("/popular")
    public ApiResponse<List<SharePortfolioBoardDTO>> getPopularPortfolio() {

        // 공유 포트폴리오에서 Import 수 상위 10개 portfolioId 및 count개수 가져오기
        List<TopPortfolioInfoResponseDTO> topPortfoliosIds = portfolioService.getTopSharePortfolioIds();
        
        // 인기 포트폴리오 10개
        List<SharePortfolioBoardDTO> topPortfolios = portfolioService.getTopSharePortfolios(topPortfoliosIds);

        return ApiResponse.onSuccess(topPortfolios);
    }

    // 전문가 포트폴리오 조회  (GET)
    @GetMapping("/expert")
    public ApiResponse<List<SharePortfolioBoardDTO>> getExpertPortfolio() {
        // 전문가 id
        String expertUserId = "80dd9d5a-758d-4bfa-b490-67a7d6489a30";

        // 공유 포트폴리오에서 전문가 Id에 해당하는 portfolioId, count개수, 생성시간 가져오기
        List<TopPortfolioInfoResponseDTO> expertPortfoliosIds = portfolioService.getExpertSharePortfolioIds(expertUserId);

        // 전문가 포트폴리오
        List<SharePortfolioBoardDTO> expertPortfolios = portfolioService.getTopSharePortfolios(expertPortfoliosIds);

        return ApiResponse.onSuccess(expertPortfolios);
    }

    @PostMapping("/my/graph")
    public ApiResponse<PortfolioMarketGraphResponseDTO> getMyPortfolioMarketGraph(@RequestBody PortfolioMarketGraphRequestDTO request,
                                                                                  @RequestParam(value = "market") Market market,
                                                                                  @RequestHeader(value = "Authorization", required = false) String token) {
        PortfolioMarketGraphResponseDTO response = portfolioService.getMyPortfolioMarketGraph(request, market, token);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/share/graph")
    public ApiResponse<PortfolioMarketGraphResponseDTO> getSharePortfolioMarketGraph(@RequestBody PortfolioMarketGraphRequestDTO request,
                                                                                  @RequestParam(value = "market") Market market) {
        PortfolioMarketGraphResponseDTO response = portfolioService.getSharePortfolioMarketGraph(request, market);
        return ApiResponse.onSuccess(response);
    }
}
