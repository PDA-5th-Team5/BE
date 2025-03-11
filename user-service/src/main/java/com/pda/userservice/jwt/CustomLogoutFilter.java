package com.pda.userservice.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pda.userservice.entity.Refresh;
import com.pda.userservice.repository.RefreshRepository;
import com.pda.utilservice.jwt.JWTUtil;
import com.pda.utilservice.response.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        //get refresh token
        String refresh = null;
        refresh = request.getHeader("Refresh");

//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//
//            if (cookie.getName().equals("refresh")) {
//
//                refresh = cookie.getValue();
//            }
//        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        //refresh null check
        if (refresh == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);// 최종 응답 DTO 생성
            ApiResponse<Void> responseDTO = ApiResponse.onFailure(HttpServletResponse.SC_BAD_REQUEST, "로그아웃 실패");
            new ObjectMapper().writeValue(response.getWriter(), responseDTO);
            return;
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);// 최종 응답 DTO 생성
            ApiResponse<Void> responseDTO = ApiResponse.onFailure(HttpServletResponse.SC_BAD_REQUEST, "로그아웃 실패");
            new ObjectMapper().writeValue(response.getWriter(), responseDTO);
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);// 최종 응답 DTO 생성
            ApiResponse<Void> responseDTO = ApiResponse.onFailure(HttpServletResponse.SC_BAD_REQUEST, "로그아웃 실패");
            new ObjectMapper().writeValue(response.getWriter(), responseDTO);
            return;
        }

        String username = jwtUtil.getUsername(refresh);

        // DB에 저장되어 있는지 확인
        Optional<Refresh> byId = refreshRepository.findById(username);
        if (byId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);// 최종 응답 DTO 생성
            ApiResponse<Void> responseDTO = ApiResponse.onFailure(HttpServletResponse.SC_BAD_REQUEST, "로그아웃 실패");
            new ObjectMapper().writeValue(response.getWriter(), responseDTO);
            return;
        }

        Refresh refreshEntity = byId.get();
        if (!refreshEntity.getRefresh().equals(refresh)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);// 최종 응답 DTO 생성
            ApiResponse<Void> responseDTO = ApiResponse.onFailure(HttpServletResponse.SC_BAD_REQUEST, "로그아웃 실패");
            new ObjectMapper().writeValue(response.getWriter(), responseDTO);
            return;
        }

        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
        refreshRepository.deleteById(username);

        //Refresh 토큰 Cookie 값 0
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
        ApiResponse<Void> responseDTO = ApiResponse.onSuccess(HttpServletResponse.SC_OK, "로그아웃 성공");
        new ObjectMapper().writeValue(response.getWriter(), responseDTO);
    }
}