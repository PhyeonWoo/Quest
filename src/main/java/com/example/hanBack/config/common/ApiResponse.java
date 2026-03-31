package com.example.hanBack.config.common;

import lombok.Builder;

@Builder
public record ApiResponse<T>(
        String success,
        String code,
        String message,
        T data
) {
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success("true")
                .code(builder().code)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> fail(String statusCode, String message, T data) {
        return ApiResponse.<T>builder()
                .success("false")
                .code(statusCode)
                .message(message)
                .data(data)
                .build();
    }
}