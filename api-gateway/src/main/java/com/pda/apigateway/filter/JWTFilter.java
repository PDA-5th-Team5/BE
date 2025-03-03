package com.pda.apigateway.filter;

import com.pda.apigateway.filter.CustomUserDetails;
import com.pda.utilservice.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("JWTFilter.doFilterInternal API Gateway");

        // 필터를 무시할 URL 패턴 설정
        List<String> excludeUrlPatterns = Arrays.asList(
                "/user",
                "/user/join",
                "/user/login",
                "/user/logout",
                "/user/reissue",
                "/portfolio/share(\\?sort=.*&page=\\d+&size=\\d+)?",
                "/portfolio/share/\\d+/import",
                "/portfolio/share/\\d+/summary",
                "/portfolio/share/\\d+/graph",
                "/portfolio/share/\\d+/snowflake",
                "/portfolio/share/\\d+/stocks(\\?sort=.*)?",
                "/portfolio/share/\\d+/comments",
                "/stocks/\\d+",
                "/stocks/\\d+/candle",
                "/stocks/\\d+/competitors(\\?sector=.*)?",
                "/stocks/\\d+/graph",
                "/stocks/\\d+/comments(\\?page=\\d+&size=\\d+)?",
                "/stocks/search(\\?keyword=.*)?",
                "/snowflake",
                "/snowflake/result(\\?sort=.*&page=\\d+&size=\\d+)?",
                "/snowflake/elements/graph(\\?elementType=.*)?"
        );


        // 현재 요청 URL 가져오기
        String requestURI = request.getRequestURI();

        // 현재 요청이 필터 제외 대상인지 확인
        boolean isExcluded = excludeUrlPatterns.stream()
                .anyMatch(pattern -> Pattern.matches(pattern, requestURI));

        if (isExcluded) {
            System.out.println("Skipping JWT filter for: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //request에서 Authorization 헤더를 찾음
        String authorization= request.getHeader("Authorization");

        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            System.out.println("token null");
            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        System.out.println("authorization now");
        //Bearer 부분 제거 후 순수 토큰만 획득
        String accessToken = authorization.split(" ")[1];

//        // 헤더에서 access키에 담긴 토큰을 꺼냄
//        String accessToken = request.getHeader("access");

//        // 토큰이 없다면 다음 필터로 넘김
//        if (accessToken == null) {
//
//            filterChain.doFilter(request, response);
//
//            return;
//        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // username, role 값을 획득
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        CustomUserDetails customUserDetails = new CustomUserDetails(username, role);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
