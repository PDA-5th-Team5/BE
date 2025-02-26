package com.pda.userservice.entity;

import com.pda.userservice.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(length = 36, nullable = false, unique = true)
    private String userId;

    private String nickname;

    private String username;

    private String passwordHash;

    private String email;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @PrePersist
    public void prePersist() {
        if (this.userId == null) {
            this.userId = UUID.randomUUID().toString();
        }
    }
}
