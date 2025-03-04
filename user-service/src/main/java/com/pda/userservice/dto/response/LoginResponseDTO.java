package com.pda.userservice.dto.response;

import com.pda.userservice.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDTO {
    private String userId;
    private String nickname;
    private String email;

    public static LoginResponseDTO toDTO(User user) {
        return LoginResponseDTO.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }
}
