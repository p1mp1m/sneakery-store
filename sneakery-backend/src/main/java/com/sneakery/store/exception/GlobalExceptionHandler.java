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
        ErrorResponseDto.ErrorResponseDtoBuilder builder = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI());
        
        // Thêm errorCode nếu có
        if (ex.getErrorCode() != null) {
            builder.errorCode(ex.getErrorCode());
        }
        
        ErrorResponseDto errorDto = builder.build();

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
                .errorCode("BAD_CREDENTIALS")
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
                .errorCode("VALIDATION_ERROR")
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
        String errorCode = "DATA_INTEGRITY_VIOLATION";
        
        if (message != null) {
            String lowerMessage = message.toLowerCase();
            if (lowerMessage.contains("foreign key") || lowerMessage.contains("fk_") || lowerMessage.contains("references")) {
                userMessage = "Không thể xóa vì dữ liệu đang được sử dụng. Vui lòng xóa hoặc cập nhật các dữ liệu liên quan trước.";
                errorCode = "FOREIGN_KEY_CONSTRAINT";
            } else if (lowerMessage.contains("unique constraint") || lowerMessage.contains("unique") || lowerMessage.contains("duplicate")) {
                userMessage = "Dữ liệu đã tồn tại. Vui lòng kiểm tra lại.";
                errorCode = "UNIQUE_CONSTRAINT_VIOLATION";
            } else if (lowerMessage.contains("check constraint") || lowerMessage.contains("ck_")) {
                userMessage = "Dữ liệu không đáp ứng yêu cầu. Vui lòng kiểm tra lại các giá trị nhập vào.";
                errorCode = "CHECK_CONSTRAINT_VIOLATION";
            }
        }
        
        log.warn("Data integrity violation at {}: {} - Error code: {}", request.getRequestURI(), message, errorCode);
        
        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(userMessage)
                .errorCode(errorCode)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    /**
     * 5. Bắt lỗi 'IllegalArgumentException' (lỗi tham số không hợp lệ)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        log.warn("Illegal argument at {}: {}", request.getRequestURI(), ex.getMessage());
        
        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage() != null ? ex.getMessage() : "Tham số không hợp lệ")
                .errorCode("ILLEGAL_ARGUMENT")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * 6. Bắt lỗi 'AccessDeniedException' (lỗi không có quyền truy cập)
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException ex,
            HttpServletRequest request
    ) {
        log.warn("Access denied at {}: {}", request.getRequestURI(), ex.getMessage());
        
        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message("Bạn không có quyền truy cập tài nguyên này")
                .errorCode("ACCESS_DENIED")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.FORBIDDEN);
    }

    /**
     * 7. Bắt lỗi 'MethodArgumentTypeMismatchException' (lỗi type mismatch)
     */
    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatchException(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        log.warn("Method argument type mismatch at {}: {}", request.getRequestURI(), ex.getMessage());
        
        String message = String.format("Tham số '%s' không đúng định dạng. Giá trị '%s' không hợp lệ.", 
                ex.getName(), ex.getValue());
        
        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .errorCode("TYPE_MISMATCH")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * 8. Bắt lỗi 'HttpRequestMethodNotSupportedException' (lỗi HTTP method không được hỗ trợ)
     * Override method từ parent class để tránh xung đột
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            @NonNull org.springframework.web.HttpRequestMethodNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        log.warn("HTTP method not supported at {}: {}", 
                ((org.springframework.web.context.request.ServletWebRequest) request).getRequest().getRequestURI(), 
                ex.getMessage());
        
        String supportedMethods = ex.getSupportedMethods() != null 
                ? String.join(", ", ex.getSupportedMethods())
                : "N/A";
        
        String message = String.format("Phương thức HTTP '%s' không được hỗ trợ. Các phương thức được hỗ trợ: %s", 
                ex.getMethod(), supportedMethods);
        
        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())
                .message(message)
                .errorCode("METHOD_NOT_ALLOWED")
                .path(((org.springframework.web.context.request.ServletWebRequest) request).getRequest().getRequestURI())
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * 9. Bắt tất cả các lỗi 500 (lỗi server) khác
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
                .errorCode("INTERNAL_SERVER_ERROR")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}