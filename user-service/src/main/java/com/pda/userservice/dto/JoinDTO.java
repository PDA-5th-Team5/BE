package com.pda.userservice.dto;

import com.pda.userservice.entity.User;
import com.pda.userservice.enums.UserType;
import lombok.Builder;
import lombok.Data;

@Data
public class JoinDTO {
    private String username;
    private String password;
    private String nickname;
    private String email;

    public User toUserEntity(String encodedPassword) {
        return User.builder()
                .username(this.username)
                .passwordHash(encodedPassword)
                .userType(UserType.ROLE_ADMIN)
                .nickname(this.nickname)
                .email(this.email)
                .build();
    }
}
