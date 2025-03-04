package com.pda.userservice.service;

import com.pda.userservice.dto.request.JoinDTO;
import com.pda.userservice.dto.response.NicknameResponseDTO;
import com.pda.userservice.entity.Refresh;
import com.pda.userservice.entity.User;
import com.pda.userservice.repository.UserRepository;
import com.pda.userservice.repository.RefreshRepository;
import com.pda.utilservice.jwt.JWTUtil;
import com.pda.utilservice.response.ApiResponse;
import com.pda.utilservice.response.code.resultCode.ErrorStatus;
import com.pda.utilservice.response.exception.handler.UserHandler;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RefreshRepository refreshRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment environment;


    @Override
    public ApiResponse<Void> join(JoinDTO joinDTO) {
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        // 아이디 중복 확인
        if (userRepository.existsByUsername(username)) {
            return ApiResponse.onSuccess(HttpStatus.CONFLICT.value(), "중복된 아이디 입니다.");
        }

        // 사용자 정보 생성 및 저장
        User user = joinDTO.toUserEntity(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);  // DB에 저장
        return ApiResponse.onSuccess(HttpStatus.OK.value(), "회원가입 성공");
    }

    @Override
    public ApiResponse<Void> handleReissue(HttpServletRequest request, HttpServletResponse response) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));

        String refresh = extractRefreshToken(request);

        // Refresh 토큰이 헤더에 있는지 확인
        if (refresh == null) {
            return ApiResponse.onFailure(HttpStatus.BAD_REQUEST.value(), "토큰 재발급 실패(토큰 없음)");
        }

        // Refresh 토큰 만료 여부 확인
        if (isTokenExpired(refresh)) {
            return ApiResponse.onFailure(HttpStatus.BAD_REQUEST.value(), "토큰 재발급 실패(토큰 만료)");
        }

        // Refresh 토큰이 맞는지 확인
        if (!isRefreshTokenValid(refresh)) {
            return ApiResponse.onFailure(HttpStatus.BAD_REQUEST.value(), "토큰 재발급 실패(refresh 토큰이 아님");
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // DB에 토큰 존재 여부 확인
        if (!isTokenStoredInDB(refresh, username)) {
            return ApiResponse.onFailure(HttpStatus.BAD_REQUEST.value(), "토큰 재발급 실패(DB에 없음)");
        }

        // userId 조회
        String userId = userRepository.findByUsername(username).getUserId();

        String newAccess = jwtUtil.createJwt("access", userId, username, role, 600000L); // 10분 유효기간
        response.setHeader("access", newAccess);

        return ApiResponse.onSuccess(HttpStatus.OK.value(), "토큰 재발급 성공");
    }

    @Override
    public NicknameResponseDTO getNicknameByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        return NicknameResponseDTO.toDTO(user);
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean isTokenExpired(String token) {
        try {
            JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
            jwtUtil.isExpired(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private boolean isRefreshTokenValid(String token) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
        return "refresh".equals(jwtUtil.getCategory(token));
    }

    private boolean isTokenStoredInDB(String token, String username) {
        Optional<Refresh> byId = refreshRepository.findById(username);
        return byId.isPresent() && token.equals(byId.get().getRefresh());
    }
}
