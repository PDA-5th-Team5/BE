package com.pda.userservice.service;

import com.pda.userservice.dto.JoinDTO;
import com.pda.userservice.entity.User;
import com.pda.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public boolean join(JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        // 아이디 중복 확인
        if (userRepository.existsByUsername(username)) {
            System.out.println("중복된 아이디입니다.");  // 로그 기록
            return false;  // 중복 시 실패 반환
        }

        // 사용자 정보 생성 및 저장
        User user = joinDTO.toUserEntity(bCryptPasswordEncoder.encode(password));

        userRepository.save(user);  // DB에 저장
        return true;  // 성공 시 true 반환
    }
}
