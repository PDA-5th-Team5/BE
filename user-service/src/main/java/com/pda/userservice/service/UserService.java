package com.pda.userservice.service;

import com.pda.userservice.dto.request.JoinDTO;
import com.pda.userservice.dto.request.ProfileRequestDTO;
import com.pda.userservice.dto.response.CommentsResponseDTO;
import com.pda.userservice.dto.response.NicknameResponseDTO;
import com.pda.userservice.dto.response.StocksResponseDTO;
import com.pda.utilservice.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

    /**
     * 사용자 회원가입 처리
     * @param joinDTO 회원가입 요청 데이터
     * @return 가입 성공 여부
     */
    ApiResponse<Void> join(JoinDTO joinDTO);

    /**
     * Refresh Token을 사용하여 Access Token 재발급 처리
     * @param request HTTP 요청 객체 (쿠키에서 Refresh Token 추출)
     * @param response HTTP 응답 객체 (새로운 Access Token 설정)
     * @return 재발급 결과에 대한 ResponseEntity
     */
    ApiResponse<Void> handleReissue(HttpServletRequest request, HttpServletResponse response);

    NicknameResponseDTO getNicknameByUserId(String userId);

    ApiResponse<Void> profile(ProfileRequestDTO profileRequestDTO, String token);

    ApiResponse<CommentsResponseDTO> comments(String token);

    void updateTelegramChatId(String userId, String chatId);
    String getTelegramChatId(String token);
    void deleteTelegramChatId(String token);
    ApiResponse<StocksResponseDTO> stocks(String token);

    String getTelegramChatIdUserId(String userId);

}
