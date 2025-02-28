package com.pda.userservice.service;

import com.pda.userservice.dto.request.JoinDTO;
import com.pda.userservice.dto.response.NicknameResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {

    /**
     * 사용자 회원가입 처리
     * @param joinDTO 회원가입 요청 데이터
     * @return 가입 성공 여부
     */
    boolean join(JoinDTO joinDTO);

    /**
     * Refresh Token을 사용하여 Access Token 재발급 처리
     * @param request HTTP 요청 객체 (쿠키에서 Refresh Token 추출)
     * @param response HTTP 응답 객체 (새로운 Access Token 설정)
     * @return 재발급 결과에 대한 ResponseEntity
     */
    ResponseEntity<?> handleReissue(HttpServletRequest request, HttpServletResponse response);

    NicknameResponseDTO getNicknameByUserId(String userId);
}
