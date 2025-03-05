package com.pda.userservice.dto.request;

import com.pda.userservice.entity.User;
import lombok.Data;

@Data
public class ProfileRequestDTO {
    private String nickname;
    private String email;

    public User toUserEntity(User existingUser) {
        return User.builder()
                .userId(existingUser.getUserId())  // 기존 userId 유지
                .nickname(this.nickname)  // DTO에서 받은 값 적용
                .username(existingUser.getUsername())  // 기존 username 유지
                .passwordHash(existingUser.getPasswordHash())  // 기존 password 유지
                .email(this.email)  // DTO에서 받은 값 적용
                .createdAt(existingUser.getCreatedAt())  // 기존 생성일 유지
                .userType(existingUser.getUserType())  // 기존 userType 유지
                .build();
    }
}
