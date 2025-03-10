package com.pda.portfolioservice.controller;

import com.pda.portfolioservice.dto.request.PortfolioAlertRequestDTO;
import com.pda.portfolioservice.dto.response.PortfolioAlertResponseDTO;
import com.pda.portfolioservice.service.PortfolioAlertService;
import com.pda.utilservice.jwt.JWTUtil;
import com.pda.utilservice.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/portfolio/alerts")
@RequiredArgsConstructor
public class PortfolioAlertController {

    private final PortfolioAlertService portfolioAlertService;
    private final Environment environment;

    /**
     *  1. 알림 추가 API
     */
    @PostMapping
    public ApiResponse<Void> addAlert(@RequestBody PortfolioAlertRequestDTO request, @RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return ApiResponse.onFailure(401, "인증 토큰이 필요합니다.");
        }
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
        String userId = jwtUtil.getBearerUserId(token);

        return portfolioAlertService.addAlert(request, userId);
    }

    /**
     *  2. 알림 삭제 API
     */
    @DeleteMapping("/{portfolioId}")
    public ApiResponse<Void> deleteAlert(@PathVariable Long portfolioId, @RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return ApiResponse.onFailure(401, "인증 토큰이 필요합니다.");
        }
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
        String userId = jwtUtil.getBearerUserId(token);

        return portfolioAlertService.deleteAlert(portfolioId, userId);
    }

    /**
     *  3. 내 알림 조회 API
     */
    @GetMapping
    public ApiResponse<List<PortfolioAlertResponseDTO>> getUserAlerts(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return ApiResponse.onFailure(401, "인증 토큰이 필요합니다.",null);
        }
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
        String userId = jwtUtil.getBearerUserId(token);

        return portfolioAlertService.getUserAlerts(userId);
    }
}