package com.pda.portfolioservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "userservice")
public interface UserServiceClient {

}
