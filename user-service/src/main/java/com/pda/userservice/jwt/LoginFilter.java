package com.pda.userservice.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pda.userservice.dto.response.LoginResponseDTO;
import com.pda.userservice.entity.Refresh;
import com.pda.userservice.entity.User;
import com.pda.userservice.repository.RefreshRepository;
import com.pda.userservice.repository.UserRepository;
import com.pda.utilservice.jwt.JWTUtil;
import com.pda.utilservice.response.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        System.out.println("LoginFilter.attemptAuthentication");

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        System.out.println("username = " + username);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        //유저 정보
        String username = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // user 조회
        User user = userRepository.findByUsername(username);

        String userId = user.getUserId();

        //토큰 생성
//        String access = jwtUtil.createJwt("access", userId, username, role, 600000L); // 10분
        String access = jwtUtil.createJwt("access", userId, username, role, 604800000L); // 7일
        String refresh = jwtUtil.createJwt("refresh", userId, username, role, 86400000L);

        // Refresh 토큰 DB에 저장
//        addRefreshEntity(username, refresh, 86400000L); // mysql
        addRefreshEntity(username, refresh); // redis

        // 로그인 응답 DTO 변환
        LoginResponseDTO loginResponse = LoginResponseDTO.toDTO(user);

        // 최종 응답 DTO 생성
        ApiResponse<LoginResponseDTO> responseDTO = ApiResponse.onSuccess(loginResponse);


        // JSON 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("access", access);
//        response.setHeader("Authorization", "Bearer " + access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());

        new ObjectMapper().writeValue(response.getWriter(), responseDTO);
    }

    // TODO to redis
    private void addRefreshEntity(String username, String refresh) {

//        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refreshEntity = new Refresh();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
//        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);// 최종 응답 DTO 생성
        ApiResponse<Void> responseDTO = ApiResponse.onFailure(HttpServletResponse.SC_BAD_REQUEST, "로그인 실패");
        new ObjectMapper().writeValue(response.getWriter(), responseDTO);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
