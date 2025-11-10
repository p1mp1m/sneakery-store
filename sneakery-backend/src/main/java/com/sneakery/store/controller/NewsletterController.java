package com.sneakery.store.controller;

import com.sneakery.store.dto.NewsletterSubscriptionDto;
import com.sneakery.store.service.NewsletterService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller xử lý Newsletter Subscriptions (Public)
 */
@Tag(name = "Newsletter", description = "API đăng ký nhận tin")
@Slf4j
@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class NewsletterController {

    private final NewsletterService newsletterService;

    /**
     * Đăng ký nhận tin mới (Public)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Validate email format</li>
     *   <li>Kiểm tra email đã đăng ký chưa</li>
     *   <li>Nếu chưa đăng ký: Tạo subscription mới</li>
     *   <li>Nếu đã đăng ký nhưng đã unsubscribe: Reactivate</li>
     *   <li>Nếu đã đăng ký và đang active: Trả về lỗi</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Endpoint này công khai, không cần đăng nhập</li>
     *   <li>Email phải hợp lệ và unique</li>
     *   <li>Nếu email đã đăng ký nhưng đã unsubscribe, sẽ được reactivate</li>
     * </ul>
     * 
     * @param request DTO chứa email cần đăng ký
     * @return ResponseEntity chứa NewsletterSubscriptionDto (HTTP 201 Created)
     * @throws ApiException nếu email không hợp lệ hoặc đã đăng ký
     * 
     * @example
     * <pre>
     * NewsletterSubscribeRequest request = new NewsletterSubscribeRequest();
     * request.setEmail("user@example.com");
     * ResponseEntity&lt;NewsletterSubscriptionDto&gt; response = newsletterController.subscribe(request);
     * </pre>
     */
    @Operation(summary = "Đăng ký nhận tin", description = "Đăng ký email để nhận thông báo về sản phẩm mới và ưu đãi đặc biệt. Endpoint công khai, không cần đăng nhập.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Đăng ký thành công"),
        @ApiResponse(responseCode = "400", description = "Email không hợp lệ"),
        @ApiResponse(responseCode = "409", description = "Email đã đăng ký")
    })
    @PostMapping("/subscribe")
    public ResponseEntity<NewsletterSubscriptionDto> subscribe(@Valid @RequestBody NewsletterSubscribeRequest request) {
        log.info("📍 POST /api/newsletter/subscribe - Email: {}", request.getEmail());
        NewsletterSubscriptionDto subscription = newsletterService.subscribe(request.getEmail());
        return new ResponseEntity<>(subscription, HttpStatus.CREATED);
    }

    /**
     * Hủy đăng ký nhận tin (Public)
     * 
     * @param request DTO chứa email cần hủy đăng ký
     * @return ResponseEntity với message thành công (HTTP 200 OK)
     * @throws ApiException nếu email không tồn tại
     */
    @Operation(summary = "Hủy đăng ký nhận tin", description = "Hủy đăng ký email khỏi danh sách nhận tin. Endpoint công khai, không cần đăng nhập.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hủy đăng ký thành công"),
        @ApiResponse(responseCode = "404", description = "Email chưa đăng ký")
    })
    @PostMapping("/unsubscribe")
    public ResponseEntity<MessageResponse> unsubscribe(@Valid @RequestBody NewsletterUnsubscribeRequest request) {
        log.info("📍 POST /api/newsletter/unsubscribe - Email: {}", request.getEmail());
        newsletterService.unsubscribe(request.getEmail());
        return ResponseEntity.ok(new MessageResponse("Hủy đăng ký thành công"));
    }

    @Data
    public static class NewsletterSubscribeRequest {
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không hợp lệ")
        private String email;
    }

    @Data
    public static class NewsletterUnsubscribeRequest {
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không hợp lệ")
        private String email;
    }

    @Data
    @RequiredArgsConstructor
    public static class MessageResponse {
        private final String message;
    }
}

