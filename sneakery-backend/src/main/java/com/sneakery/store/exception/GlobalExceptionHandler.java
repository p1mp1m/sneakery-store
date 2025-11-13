package com.sneakery.store.exception;

import com.sneakery.store.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global Exception Handler - Xử lý tất cả các exception trong ứng dụng
 * và chuyển đổi chúng thành JSON Response thân thiện với user.
 * 
 * Security: Không expose internal error details ra ngoài production.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 1. Bắt lỗi 'ApiException' (lỗi 404, 400 do chúng ta chủ động ném ra)
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponseDto> handleApiException(
            ApiException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = ex.getStatus();
        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorDto, status);
    }

    /**
     * 2. Bắt lỗi 'BadCredentialsException' (lỗi xác thực - email/password sai)
     * Security: Không expose chi tiết lỗi để tránh user enumeration attack
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsException(
            org.springframework.security.authentication.BadCredentialsException ex,
            HttpServletRequest request
    ) {
        log.warn("Bad credentials attempt at {}: {}", request.getRequestURI(), ex.getMessage());
        
        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message("Email hoặc mật khẩu không đúng")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 3. Bắt lỗi 'MethodArgumentNotValidException' (lỗi validation @Valid)
     * Đây là lỗi khi DTO (ví dụ: RegisterDto) không hợp lệ
     * (ví dụ: email trống, password < 6 ký tự)
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        // Lấy danh sách các lỗi
        Map<String, List<String>> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Dữ liệu đầu vào không hợp lệ")
                .path(((org.springframework.web.context.request.ServletWebRequest) request).getRequest().getRequestURI())
                .validationErrors(errors) // Thêm chi tiết lỗi
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * 4. Bắt lỗi 'DataIntegrityViolationException' (lỗi foreign key constraint, unique constraint...)
     * Đây là lỗi khi vi phạm ràng buộc dữ liệu trong database
     */
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(
            org.springframework.dao.DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        log.warn("Data integrity violation at {}: {}", request.getRequestURI(), ex.getMessage());
        
        // Kiểm tra xem có phải foreign key constraint không
        String message = ex.getMessage();
        String userMessage = "Không thể thực hiện thao tác này vì dữ liệu đang được sử dụng ở nơi khác.";
        
        if (message != null) {
            if (message.contains("foreign key") || message.contains("FK_") || message.contains("REFERENCES")) {
                userMessage = "Không thể xóa vì dữ liệu đang được sử dụng. Vui lòng xóa hoặc cập nhật các dữ liệu liên quan trước.";
            } else if (message.contains("unique constraint") || message.contains("UNIQUE") || message.contains("duplicate")) {
                userMessage = "Dữ liệu đã tồn tại. Vui lòng kiểm tra lại.";
            }
        }
        
        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(userMessage)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    /**
     * 5. Bắt tất cả các lỗi 500 (lỗi server) khác
     * (Ví dụ: NullPointerException, lỗi CSDL...)
     * 
     * Security: Không expose internal error details ra ngoài.
     * Chỉ log chi tiết lỗi, user chỉ nhận message chung.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(
            Exception ex,
            HttpServletRequest request
    ) {
        // Log chi tiết lỗi để debug (không expose ra user)
        log.error("Internal server error occurred at {}: {}", 
                request.getRequestURI(), 
                ex.getMessage(), 
                ex);

        // User chỉ nhận message chung, không có chi tiết internal
        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau hoặc liên hệ quản trị viên.")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}