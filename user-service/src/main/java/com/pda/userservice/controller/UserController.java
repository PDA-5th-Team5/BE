package com.pda.userservice.controller;

import com.pda.userservice.dto.request.JoinDTO;
import com.pda.userservice.dto.request.ProfileRequestDTO;
import com.pda.userservice.dto.request.TelegramRegisterRequest;
import com.pda.userservice.dto.response.CommentsResponseDTO;
import com.pda.userservice.dto.response.NicknameResponseDTO;
import com.pda.userservice.dto.response.StocksResponseDTO;
import com.pda.userservice.feign.StockServiceClient;
import com.pda.userservice.service.UserService;
import com.pda.utilservice.jwt.JWTUtil;
import com.pda.utilservice.response.ApiResponse;
import com.pda.utilservice.response.code.resultCode.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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
    public NicknameResponseDTO getNickname(@PathVariable("userId") String userId) {
        System.out.println("UserController.getNickname");
        return userService.getNicknameByUserId(userId);
    }

    @PatchMapping("/profile")
    public ApiResponse<Void> profile(@RequestBody ProfileRequestDTO profileDTO, @RequestHeader(value = "Authorization", required = false) String token) {
        return userService.profile(profileDTO, token);
    }

    @GetMapping("/comments")
    public ApiResponse<CommentsResponseDTO> comments(@RequestHeader(value = "Authorization", required = false) String token) {

        return userService.comments(token);
    }

    @GetMapping("/stocks")
    public ApiResponse<StocksResponseDTO> stocks(@RequestHeader(value = "Authorization", required = false) String token) {

        return userService.stocks(token);
    }

    @PostMapping("/telegram")
    public ApiResponse<Void> registerTelegramChatId(
            @RequestBody TelegramRegisterRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {

        //  인증 토큰 검증
        if (token == null || token.isEmpty()) {
            return ApiResponse.onFailure(ErrorStatus.UNAUTHORIZED.getStatus(), "인증 토큰이 필요합니다.");
        }

        //  텔레그램 Chat ID 검증
        if (request.getChatId() == null || request.getChatId().trim().isEmpty()) {
            return ApiResponse.onFailure(ErrorStatus.BAD_REQUEST.getStatus(), "유효한 텔레그램 Chat ID가 필요합니다.");
        }

        userService.updateTelegramChatId(token, request.getChatId());
        return ApiResponse.onSuccess(201, "텔레그램 Chat ID 등록 완료");

    }

    @GetMapping("/telegram")
    public ApiResponse<String> getTelegramChatId(
            @RequestHeader(value = "Authorization", required = false) String token) {

        //  인증 토큰 검증
        if (token == null || token.isEmpty()) {
            return ApiResponse.onFailure(ErrorStatus.UNAUTHORIZED.getStatus(), "인증 토큰이 필요합니다.", "");
        }

        String chatId = userService.getTelegramChatId(token);

        //  chatId가 없으면 빈 문자열 반환
        if (chatId == null || chatId.trim().isEmpty()) {
            return ApiResponse.onFailure(ErrorStatus.USER_NOT_FOUND.getStatus(), "등록된 텔레그램 Chat ID가 없습니다.", "");
        }

        return ApiResponse.onSuccess(chatId);
    }

    @DeleteMapping("/telegram")
    public ApiResponse<String> deleteTelegramChatId(
            @RequestHeader(value = "Authorization", required = false) String token) {

        //  인증 토큰 검증
        if (token == null || token.isEmpty()) {
            return ApiResponse.onFailure(ErrorStatus.UNAUTHORIZED.getStatus(), "인증 토큰이 필요합니다.","");
        }

        userService.deleteTelegramChatId(token);
        return ApiResponse.onSuccess("텔레그램 Chat ID 삭제 완료");
    }

    @GetMapping("/telegram/{userId}")
    public ApiResponse<String> getTelegramChatIdUserId(@PathVariable("userId") String userId) {
        String chatId = userService.getTelegramChatIdUserId(userId);

        if (chatId == null || chatId.isEmpty()) {
            return ApiResponse.onFailure(404, "등록된 텔레그램 Chat ID가 없습니다.",null);
        }

        return ApiResponse.onSuccess(chatId);
    }
}


