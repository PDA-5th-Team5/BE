package com.pda.userservice.entity;

import com.pda.userservice.enums.UserType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class User {

    @Id
    @Column(length = 36, nullable = false, unique = true)
    private String userId;

    private String nickname;

    private String passwordHash;

    private String email;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private UserType userType;
}
