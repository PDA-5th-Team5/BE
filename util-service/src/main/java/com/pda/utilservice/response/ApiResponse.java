package com.pda.utilservice.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.pda.utilservice.response.code.resultCode.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"status", "message", "data" })
public class ApiResponse<T> {

    private final int status;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    // 성공한 경우 응답 생성
    public static <T> ApiResponse<T> onSuccess(T data) {
        return new ApiResponse<>(SuccessStatus.OK.getStatus(), SuccessStatus.OK.getMessage(), data);
    }

    // 성공한 경우 응답 (데이터 없음)
    public static ApiResponse<Void> onSuccess(int status, String message) {
        return new ApiResponse<>(status, message, null);
    }

    // 실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(int status, String message, T data) {
        return new ApiResponse<>(status, message, data);
    }

    // 실패 응답 (데이터 없음)
    public static ApiResponse<Void> onFailure(int status, String message) {
        return new ApiResponse<>(status, message, null);
    }


}
