package back.Quest.config.exception;


import back.Quest.config.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(CustomException.NotFoundException e) {
        log.warn("Not Found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(HttpStatus.NOT_FOUND, e.getMessage(), null));
    }

    @ExceptionHandler(CustomException.DuplicateException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateException(CustomException.DuplicateException e) {
        log.warn("Duplicate: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(HttpStatus.CONFLICT, e.getMessage(), null));
    }

    @ExceptionHandler(CustomException.InvalidRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRequestException(CustomException.InvalidRequestException e) {
        log.warn("Invalid Request: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, e.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("입력값이 올바르지 않습니다.");
        log.warn("Validation Failed: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, message, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllException(Exception e) {
        log.error("Server Error: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.", null));
    }
}