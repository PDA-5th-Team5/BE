package com.pda.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(Customizer.withDefaults())  // 최신 방식 (기본 설정 사용 및 필요 시 Customizer 설정)
                .csrf(csrf -> csrf.disable())     // CSRF 비활성화 (deprecated 해결 방법)
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()   // 모든 경로 허용
                )
                .httpBasic(Customizer.withDefaults()) // 기본 HTTP 인증 비활성화
                .formLogin(Customizer.withDefaults()); // 폼 로그인 비활성화

        return http.build();
    }
}

