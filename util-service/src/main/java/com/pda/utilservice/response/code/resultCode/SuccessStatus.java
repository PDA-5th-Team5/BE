package com.pda.utilservice.response.code.resultCode;

import com.pda.utilservice.response.code.BaseCode;
import com.pda.utilservice.response.code.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    OK(HttpStatus.OK, 200, "성공입니다.");

    private final HttpStatus httpStatus;
    private final int status;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .status(status)
                .message(message)
                .build();
    }

    @Override
    public ReasonDto getReasonHttpStatus() {
        return ReasonDto.builder()
                .status(status)
                .message(message)
                .httpStatus(httpStatus)
                .build();
    }
}
