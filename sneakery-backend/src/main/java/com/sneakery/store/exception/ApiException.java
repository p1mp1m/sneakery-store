package com.sneakery.store.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception cho API errors
 * 
 * <p>Exception này được sử dụng để throw các lỗi API với HTTP status code và message cụ thể.
 * Được xử lý bởi GlobalExceptionHandler để trả về JSON response thân thiện với user.
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm");
 * throw new ApiException(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ", "INVALID_DATA");
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String message;
    private final String errorCode; // Error code để frontend có thể xử lý cụ thể

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
        this.errorCode = null;
    }

    public ApiException(HttpStatus status, String message, String errorCode) {
        super(message);
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getErrorCode() {
        return errorCode;
    }
}