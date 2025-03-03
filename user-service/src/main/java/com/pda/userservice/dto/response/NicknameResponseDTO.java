package com.pda.userservice.dto.response;

import com.pda.userservice.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NicknameResponseDTO {

    private String nickname;

    public static NicknameResponseDTO toDTO(User user) {
        return NicknameResponseDTO.builder()
                .nickname(user.getNickname()).build();
    }
}
