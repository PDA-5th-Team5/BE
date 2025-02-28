package com.pda.userservice.controller;

import com.pda.userservice.dto.request.JoinDTO;
import com.pda.userservice.dto.response.NicknameResponseDTO;
import com.pda.userservice.service.UserService;
import com.pda.utilservice.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        System.out.println("UserController.join");

        boolean result = userService.join(joinDTO);

        if (!result) {
            return new ResponseEntity<>("아이디가 이미 존재합니다.", CONFLICT);
        }

        return new ResponseEntity<>("회원가입 성공", HttpStatus.OK);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        return userService.handleReissue(request, response);
    }

    @GetMapping("/{userId}/nickname")
    public ApiResponse<NicknameResponseDTO> getNickname(@PathVariable String userId) {
        System.out.println("UserController.getNickname");
        NicknameResponseDTO nicknameResponseDTO = userService.getNicknameByUserId(userId);
        return ApiResponse.onSuccess(nicknameResponseDTO);
    }
}
