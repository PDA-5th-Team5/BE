package com.pda.stockservice.feign;

<<<<<<< HEAD

=======
>>>>>>> 0741845 (feat(#51): 주식임계값 엔티티)
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {
<<<<<<< HEAD

    @GetMapping("/{userId}/nickname")
    String getNickname(@PathVariable("userId") String userId);
}
=======
    @GetMapping("/user/{id}")
    String getUserNickname(@PathVariable("userId") String userId);
}
>>>>>>> 0741845 (feat(#51): 주식임계값 엔티티)
