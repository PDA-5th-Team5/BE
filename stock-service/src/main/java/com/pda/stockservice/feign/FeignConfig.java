package com.pda.stockservice.feign;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Decoder feignDecoder() {
        return new feign.jackson.JacksonDecoder(); // JSON을 DTO로 변환
    }


    @Bean
    public Encoder feignEncoder() {
        return new SpringFormEncoder();
    }
}