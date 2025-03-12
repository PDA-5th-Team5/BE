package com.pda.apigateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pda.apigateway.filter.CustomUserDetails;
import com.pda.utilservice.response.ApiResponse;
import com.pda.utilservice.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//            System.out.println(cookie.getName() + ":" + cookie.getValue());
//        }
        // 🔥 Prometheus 요청은 JWT 인증을 건너뛰도록 예외 처리
        if (request.getRequestURI().startsWith("/actuator/prometheus")) {
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("JWTFilter.doFilterInternal API Gateway");

        // 필터를 무시할 URL 패턴 설정
        // version 1 - prefix 생략
//        List<String> excludeUrlPatterns = Arrays.asList(
//            "/user",
//            "/user/join",
//            "/user/login",
//            "/user/logout",
//            "/user/reissue",
//            "/portfolio/share(\\?sort=.*&page=\\d+&size=\\d+)?",
//            "/portfolio/share/\\d+/import",
//            "/portfolio/share/\\d+/summary",
//            "/portfolio/share/\\d+/graph",
//            "/portfolio/share/\\d+/snowflake",
//            "/portfolio/share/\\d+/stocks(\\?sort=.*)?",
//            "/portfolio/share/\\d+/comments",
//            "/stocks/\\d+",
//            "/stocks/\\d+/candle",
//            "/stocks/\\d+/competitors(\\?sector=.*)?",
//            "/stocks/\\d+/graph",
//            "/stocks/\\d+/comments(\\?page=\\d+&size=\\d+)?",
//            "/stocks/search(\\?keyword=.*)?",
//            "/snowflake",
//            "/snowflake/result(\\?sort=.*&page=\\d+&size=\\d+)?",
//            "/snowflake/elements/graph(\\?elementType=.*)?",
//            "/stocks/sectors",
//            "/stocks/thresholds",
//            "/stocks/filter\\?page=\\d+"
//        );

        // vesion 2 - prefix 넣음
        List<String> excludeUrlPatterns = Arrays.asList(
                "/user",
                "/user/join",
                "/user/login",
                "/user/logout",
                "/user/reissue",
                "/portfolio/api/portfolio/share(\\?sort=.*&page=\\d+&size=\\d+)?",
                "/portfolio/api/portfolio/share/\\d+/import",
                "/portfolio/api/portfolio/share/\\d+/summary",
                "/portfolio/api/portfolio/share/\\d+/graph",
                "/portfolio/api/portfolio/share/\\d+/snowflake",
                "/portfolio/api/portfolio/share/\\d+/stocks(\\?sort=.*)?",
                "/portfolio/api/portfolio/share/\\d+/comments",
                "/portfolio/api/portfolio/popular",
                "/stock/api/stocks/\\d+",
                "/stock/api/stocks/\\d+/candle",
                "/stock/api/stocks/\\d+/competitors(\\?sector=.*)?",
                "/stock/api/stocks/\\d+/graph",
                "/stock/api/stocks/\\d+/comments(\\?page=\\d+&size=\\d+)?",
                "/stock/api/stocks/search(\\?keyword=.*)?",
                "/stock/api/stocks/sectors",
                "/stock/api/stocks/thresholds",
//                "/stock/api/stocks/filter\\?page=\\d+"
                "/stock/api/stocks/filter"
        );


        // 현재 요청 URL 가져오기
        String requestURI = request.getRequestURI();

        // 필터 제외 대상이면 그대로 체인에 넘김
        boolean isExcluded = excludeUrlPatterns.stream()
                .anyMatch(pattern -> Pattern.matches(pattern, requestURI));

        if (isExcluded) {
            System.out.println("Skipping JWT filter for: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 요청에서 Authorization 헤더 추출
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            System.out.println("token null");

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            // null access 토큰에 대해 403 에러와 JSON 응답 전송
            ApiResponse<Void> res = ApiResponse.onSuccess(HttpServletResponse.SC_FORBIDDEN, "Access 토큰이 없습니다.");
            new ObjectMapper().writeValue(response.getWriter(), res);
            return;
//            filterChain.doFilter(request, response);
//            return;
        }

        System.out.println("authorization now");
        // Bearer 접두어 제거 후 토큰만 획득
        String accessToken = authorization.split(" ")[1];

        // 토큰 만료 여부 확인
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            // 만료된 access 토큰에 대해 401 에러와 JSON 응답 전송
            ApiResponse<Void> res = ApiResponse.onSuccess(HttpServletResponse.SC_UNAUTHORIZED, "Access 토큰 만료");
            new ObjectMapper().writeValue(response.getWriter(), res);
            return;
        }

        // 토큰의 카테고리가 access 토큰인지 확인
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            // 유효하지 않은 access 토큰에 대해 401 에러와 JSON 응답 전송
            ApiResponse<Void> res = ApiResponse.onSuccess(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 access 토큰");
            new ObjectMapper().writeValue(response.getWriter(), res);
            return;
        }

        // 토큰에서 username과 role 값을 획득하여 인증 객체 생성
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        CustomUserDetails customUserDetails = new CustomUserDetails(username, role);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
