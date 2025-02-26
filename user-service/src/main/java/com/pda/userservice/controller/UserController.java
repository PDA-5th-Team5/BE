package com.pda.userservice.controller;

import com.pda.userservice.dto.request.JoinDTO;
import com.pda.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public String mainP() {

        return "Main Controller";
    }

    @GetMapping("/admin")
    public String adminP() {

        return "Admin Controller";
    }

    @PostMapping("/join")
    public ResponseEntity<String> join(JoinDTO joinDTO) {

        boolean result = userService.join(joinDTO);

        if (!result) {
            return new ResponseEntity<>("아이디가 이미 존재합니다.", CONFLICT);
        }

        return new ResponseEntity<>("회원가입 성공", HttpStatus.OK);
    }

}
