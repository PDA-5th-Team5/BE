package com.pda.userservice.feign;

import com.pda.userservice.dto.response.MyPortfolioCommentsResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "portfolio-service")
public interface PortfolioServiceClient {

    @GetMapping("api/portfolio/{userId}/my/comments")
    MyPortfolioCommentsResponseDTO getMyPortfolioComments(@PathVariable("userId") String userId);

}