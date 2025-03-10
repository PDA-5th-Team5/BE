package com.pda.portfolioservice.service;

import com.pda.portfolioservice.dto.request.PortfolioAlertRequestDTO;
import com.pda.portfolioservice.dto.response.PortfolioAlertResponseDTO;
import com.pda.portfolioservice.entity.MyPortfolio;
import com.pda.portfolioservice.entity.PortfolioAlert;
import com.pda.portfolioservice.repository.MyPortfolioRepository;
import com.pda.portfolioservice.repository.PortfolioAlertRepository;
import com.pda.portfolioservice.service.PortfolioAlertService;
import com.pda.utilservice.response.ApiResponse;
import com.pda.utilservice.response.code.resultCode.ErrorStatus;
import com.pda.utilservice.response.exception.handler.PortfolioHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioAlertServiceImpl implements PortfolioAlertService {

    private final PortfolioAlertRepository portfolioAlertRepository;
    private final MyPortfolioRepository myPortfolioRepository;

    /**
     *  알림 추가
     */
    @Transactional
    @Override
    public ApiResponse<Void> addAlert(PortfolioAlertRequestDTO request, String userId) {
        MyPortfolio myPortfolio = myPortfolioRepository.findById(request.getPortfolioId())
                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_NOT_FOUND));

        PortfolioAlert alert = PortfolioAlert.builder()
                .myPortfolio(myPortfolio)
                .userId(userId)
                .build();

        portfolioAlertRepository.save(alert);
        return ApiResponse.onSuccess(201, "알림이 성공적으로 추가되었습니다.");
    }

    /**
     *  알림 삭제
     */
    @Transactional
    @Override
    public ApiResponse<Void> deleteAlert(Long portfolioId, String userId) {
        portfolioAlertRepository.deleteByUserIdAndMyPortfolio_MyPortfolioId(userId, portfolioId);
        return ApiResponse.onSuccess(204, "알림이 성공적으로 삭제되었습니다.");
    }

    /**
     *  내 알림 조회
     */
    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<PortfolioAlertResponseDTO>> getUserAlerts(String userId) {
        List<PortfolioAlert> alerts = portfolioAlertRepository.findByUserId(userId);

        List<PortfolioAlertResponseDTO> response = alerts.stream()
                .map(PortfolioAlertResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ApiResponse.onSuccess(response);
    }
}