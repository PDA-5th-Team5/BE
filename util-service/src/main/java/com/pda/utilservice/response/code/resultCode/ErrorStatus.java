package com.pda.utilservice.response.code.resultCode;

import com.pda.utilservice.response.code.BaseErrorCode;
import com.pda.utilservice.response.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 일반 응답
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,500, "서버 에러"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403, "금지된 요청입니다."),

    // Stock 관련 에러 응답
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "존재하지 않는 주식 정보입니다."),
    FAVORITE_STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "존재하지 않는 관심 종목 주식 정보입니다."),

    // Portfolio 관련 응답
    PORTFOLIO_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "존재하지 않는 포트폴리오입니다.");

    private final HttpStatus httpStatus;
    private final int status;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
                .status(status)
                .message(message)
                .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
                .status(status)
                .message(message)
                .build();
    }
}
