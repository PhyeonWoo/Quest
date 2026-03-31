package back.Quest.config.common;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ApiResponse<T>(
        boolean success,
        int code,
        String message,
        T data
) {
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .data(data)
                .build();
    }

//    public static <T> ApiResponse<T> ok(String message, T data) {
//        return ApiResponse.<T>builder()
//                .success(true)
//                .code(HttpStatus.OK.value())
//                .message(message)
//                .data(data)
//                .build();
//    }

    public static <T> ApiResponse<T> fail(HttpStatus statusCode, String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(statusCode.value())
                .message(message)
                .data(data)
                .build();
    }
}