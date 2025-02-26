package com.pda.userservice.repository;

import com.pda.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    Boolean existsByUsername(String username);

    User findByUsername(String username);
}
