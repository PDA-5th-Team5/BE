package com.pda.portfolioservice.service;

import com.pda.portfolioservice.feign.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TelegramBotService {

    private final String botToken = System.getenv("TELEGRAM_BOT_TOKEN");
    @Value("${telegram.api.url}")
    private String telegramApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserServiceClient userServiceClient;

    /**
     *  Webhook을 통해 텔레그램 요청 처리
     */
    public void processTelegramUpdate(Map<String, Object> update) {
        Map<String, Object> message = (Map<String, Object>) update.get("message");
        if (message == null) return;

        Map<String, Object> chat = (Map<String, Object>) message.get("chat");
        if (chat == null) return;

        String chatId = String.valueOf(chat.get("id"));
        String text = (String) message.get("text");

        if ("/start".equals(text)) {
            //  사용자에게 chat_id 반환
            sendMessage(chatId, " Telegram Chat ID 등록 완료!\n" +
                    "등록된 ID: " + chatId);
        }
    }

    /**
     *  텔레그램 메시지 전송
     */
    public void sendMessage(String chatId, String message) {
        String url = String.format("%s/bot%s/sendMessage?chat_id=%s&text=%s",
                telegramApiUrl, botToken, chatId, message);
        System.out.println("url: "+url);
        restTemplate.getForObject(url, String.class);
    }
}