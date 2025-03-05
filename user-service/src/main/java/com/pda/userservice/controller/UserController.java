package com.pda.userservice.controller;

import com.pda.userservice.dto.request.JoinDTO;
import com.pda.userservice.dto.request.ProfileRequestDTO;
import com.pda.userservice.dto.response.CommentsResponseDTO;
import com.pda.userservice.dto.response.NicknameResponseDTO;
import com.pda.userservice.feign.StockServiceClient;
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
    public ApiResponse<Void> join(JoinDTO joinDTO) {

        return userService.join(joinDTO);
    }

    @PostMapping("/reissue")
    public ApiResponse<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        return userService.handleReissue(request, response);
    }

    @GetMapping("/{userId}/nickname")
    public ApiResponse<NicknameResponseDTO> getNickname(@PathVariable String userId) {
        NicknameResponseDTO nicknameResponseDTO = userService.getNicknameByUserId(userId);
        return ApiResponse.onSuccess(nicknameResponseDTO);
    }

    @PatchMapping("/profile")
    public ApiResponse<Void> profile(@RequestBody ProfileRequestDTO profileDTO, @RequestHeader(value = "Authorization", required = false) String token) {
        return userService.profile(profileDTO, token);
    }

    @GetMapping("/comments")
    public ApiResponse<CommentsResponseDTO> comments(@RequestHeader(value = "Authorization", required = false) String token) {

        return userService.comments(token);
    }
}
