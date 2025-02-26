package com.pda.userservice.service;

import com.pda.userservice.dto.request.JoinDTO;
import com.pda.userservice.entity.Refresh;
import com.pda.userservice.entity.User;
import com.pda.userservice.repository.UserRepository;
import com.pda.userservice.repository.RefreshRepository;
import com.pda.userservice.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RefreshRepository refreshRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

    @Override
    public boolean join(JoinDTO joinDTO) {
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        // 아이디 중복 확인
        if (userRepository.existsByUsername(username)) {
            System.out.println("중복된 아이디입니다.");  // 로그 기록
            return false;  // 중복 시 실패 반환
        }

        // 사용자 정보 생성 및 저장
        User user = joinDTO.toUserEntity(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);  // DB에 저장
        return true;  // 성공 시 true 반환
    }

    @Override
    public ResponseEntity<?> handleReissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = extractRefreshToken(request);

        if (refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.UNAUTHORIZED);
        }

        if (isTokenExpired(refresh)) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.UNAUTHORIZED);
        }

        if (!isRefreshTokenValid(refresh)) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        if (!isTokenStoredInDB(refresh, username)) {
            return new ResponseEntity<>("invalid refresh token(DB)", HttpStatus.UNAUTHORIZED);
        }

        String newAccess = jwtUtil.createJwt("access", username, role, 600000L); // 10분 유효기간
        response.setHeader("access", newAccess);

        return new ResponseEntity<>(HttpStatus.OK);
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
            jwtUtil.isExpired(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private boolean isRefreshTokenValid(String token) {
        return "refresh".equals(jwtUtil.getCategory(token));
    }

    private boolean isTokenStoredInDB(String token, String username) {
        Optional<Refresh> byId = refreshRepository.findById(username);
        return byId.isPresent() && token.equals(byId.get().getRefresh());
    }
}
