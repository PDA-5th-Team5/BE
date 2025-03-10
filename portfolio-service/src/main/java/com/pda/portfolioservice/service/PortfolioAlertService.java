package com.pda.portfolioservice.service;

import com.pda.portfolioservice.dto.request.PortfolioAlertRequestDTO;
import com.pda.portfolioservice.dto.response.PortfolioAlertResponseDTO;
import com.pda.utilservice.response.ApiResponse;

import java.util.List;

public interface PortfolioAlertService {

    /**
     *  알림 추가
     */
    ApiResponse<Void> addAlert(PortfolioAlertRequestDTO request, String userId);

    /**
     *  알림 삭제
     */
    ApiResponse<Void> deleteAlert(Long portfolioId, String userId);

    /**
     *  내 알림 조회
     */
    ApiResponse<List<PortfolioAlertResponseDTO>> getUserAlerts(String userId);
}