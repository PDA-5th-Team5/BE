package com.pda.userservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramRegisterRequest {
    private String chatId;  // 유저가 Telegram에서 받은 Chat ID
}