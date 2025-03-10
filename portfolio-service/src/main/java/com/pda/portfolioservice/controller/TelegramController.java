package com.pda.portfolioservice.controller;

import com.pda.portfolioservice.service.TelegramBotService;
import com.pda.utilservice.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/telegram")
public class TelegramController {

    private final TelegramBotService telegramBotService;

    /**
     *  Webhook 엔드포인트: 유저가 /start 입력하면 Telegram이 호출
     */
    @PostMapping("/webhook")
    public ApiResponse<Void> handleTelegramWebhook(@RequestBody Map<String, Object> update) {
        telegramBotService.processTelegramUpdate(update);
        return ApiResponse.onSuccess(200, "Webhook 처리 완료");
    }
}