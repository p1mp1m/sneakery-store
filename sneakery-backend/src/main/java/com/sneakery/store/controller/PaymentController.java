package com.sneakery.store.controller;

import com.sneakery.store.dto.ApiResponse;
import com.sneakery.store.dto.CheckoutRequestDto;
import com.sneakery.store.dto.OrderDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.service.OrderService;
import com.sneakery.store.service.PaymentGatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller xử lý callback từ payment gateways (VNPay, MoMo, etc.)
 */
@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final OrderService orderService;
    private final PaymentGatewayService paymentGatewayService;

    /**
     * Tạo URL thanh toán VNPay (User checkout với online payment)
     * Frontend sẽ gửi checkout data, backend tạo order và trả về payment URL
     * 
     * @param currentUser User đang đăng nhập
     * @param checkoutDto Checkout data từ frontend
     * @return Payment URL
     */
    @PostMapping("/vnpay/create")
    public ResponseEntity<?> createVNPayPayment(
            @AuthenticationPrincipal User currentUser,
            @RequestBody CheckoutRequestDto checkoutDto) {
        try {
            log.info("🔐 Creating VNPay payment for user: {}", currentUser.getId());
            
            // Checkout và nhận order với payment URL
            OrderDto order = orderService.createOrderFromCart(currentUser.getId(), checkoutDto);
            
            // Lấy payment URL từ order response
            String paymentUrl = order.getPaymentUrl();
            
            if (paymentUrl == null || paymentUrl.isEmpty()) {
                log.error("❌ Payment URL is null or empty for order: {}", order.getId());
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("Lỗi tạo URL thanh toán", "Không thể tạo payment URL"));
            }
            
            log.info("✅ VNPay payment URL created successfully for order: {}", order.getId());
            return ResponseEntity.ok(ApiResponse.success("Payment URL created", 
                Map.of("paymentUrl", paymentUrl, "orderId", order.getId())));
        } catch (Exception e) {
            log.error("❌ Error creating VNPay payment URL: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("Lỗi tạo URL thanh toán", e.getMessage()));
        }
    }

    /**
     * VNPay IPN (Instant Payment Notification) URL
     * Server-to-server callback từ VNPay để cập nhật kết quả thanh toán
     * 
     * QUAN TRỌNG: Endpoint này PHẢI trả về JSON với RspCode và Message
     * theo đúng format mà VNPay yêu cầu
     * 
     * @param params Các parameters từ VNPay callback
     * @return JSON response với RspCode và Message
     */
    @GetMapping("/vnpay/ipn")
    public ResponseEntity<Map<String, String>> handleVNPayIPN(@RequestParam Map<String, String> params) {
        Map<String, String> response = new HashMap<>();
        
        try {
            log.info("📥 Received VNPay IPN: {}", params);
            
            // Bước 1: Kiểm tra checksum (signature)
            boolean isValidSignature = paymentGatewayService.verifyVNPayCallback(params);
            if (!isValidSignature) {
                log.warn("❌ Invalid VNPay signature");
                response.put("RspCode", "97");
                response.put("Message", "Invalid Signature");
                return ResponseEntity.ok(response);
            }
            
            // Bước 2: Lấy thông tin từ VNPay
            String vnpTxnRef = params.get("vnp_TxnRef"); // Order ID
            String vnpResponseCode = params.get("vnp_ResponseCode");
            String vnpTransactionStatus = params.get("vnp_TransactionStatus");
            String vnpAmount = params.get("vnp_Amount");
            
            // Bước 3: Kiểm tra order tồn tại trong database
            try {
                Long orderId = Long.parseLong(vnpTxnRef);
                
                // Bước 4: Kiểm tra trạng thái giao dịch
                if ("00".equals(vnpResponseCode) && "00".equals(vnpTransactionStatus)) {
                    log.info("✅ VNPay IPN - Payment successful for order: {}", orderId);
                    
                    // Cập nhật kết quả thanh toán - Giảm inventory
                    orderService.processSuccessfulPayment(orderId);
                    
                    // Trả về success cho VNPay
                    response.put("RspCode", "00");
                    response.put("Message", "Confirm Success");
                } else {
                    log.warn("❌ VNPay IPN - Payment failed for order: {}. Code: {}", orderId, vnpResponseCode);
                    
                    // Thanh toán thất bại nhưng đã confirm
                    response.put("RspCode", "00");
                    response.put("Message", "Confirm Success");
                }
                
            } catch (NumberFormatException e) {
                log.error("❌ Invalid order ID format: {}", vnpTxnRef);
                response.put("RspCode", "01");
                response.put("Message", "Order Not Found");
            } catch (Exception e) {
                log.error("❌ Error processing payment for order: {}", vnpTxnRef, e);
                // Lỗi khi cập nhật -> VNPay sẽ retry
                response.put("RspCode", "99");
                response.put("Message", "Unknown error");
            }
            
        } catch (Exception e) {
            log.error("❌ Error processing VNPay IPN: {}", e.getMessage(), e);
            response.put("RspCode", "99");
            response.put("Message", "System Error");
        }
        
        log.info("📤 VNPay IPN Response: {}", response);
        return ResponseEntity.ok(response);
    }
    
    /**
     * VNPay return URL - User được redirect về đây sau khi thanh toán
     * 
     * @param params Các parameters từ VNPay
     * @return Redirect URL về frontend
     */
    @GetMapping("/vnpay/return")
    public String handleVNPayReturn(@RequestParam Map<String, String> params) {
        try {
            log.info("🔙 VNPay return callback: {}", params);
            
            String vnpResponseCode = params.get("vnp_ResponseCode");
            String vnpTxnRef = params.get("vnp_TxnRef"); // Order ID
            
            if ("00".equals(vnpResponseCode)) {
                // Thanh toán thành công - Redirect về trang success
                return "redirect:http://localhost:5173/checkout/success?orderId=" + vnpTxnRef;
            } else {
                // Thanh toán thất bại - Redirect về trang failed
                return "redirect:http://localhost:5173/checkout/failed?orderId=" + vnpTxnRef + "&code=" + vnpResponseCode;
            }
        } catch (Exception e) {
            log.error("Error processing VNPay return: {}", e.getMessage(), e);
            return "redirect:http://localhost:5173/checkout/failed?error=" + e.getMessage();
        }
    }
    
    /**
     * VNPay callback verification - Frontend gọi API này để verify payment
     * Endpoint này dùng cho PaymentCallback.vue
     * 
     * @param params Các parameters từ VNPay (được frontend forward)
     * @return JSON response với kết quả verification
     */
    @GetMapping("/vnpay/callback")
    public ResponseEntity<?> verifyVNPayCallback(@RequestParam Map<String, String> params) {
        try {
            log.info("🔍 Verifying VNPay callback from frontend: {}", params);
            
            // Bước 1: Kiểm tra checksum (signature)
            boolean isValidSignature = paymentGatewayService.verifyVNPayCallback(params);
            if (!isValidSignature) {
                log.warn("❌ Invalid VNPay signature");
                return ResponseEntity.ok(ApiResponse.error(
                    "Chữ ký không hợp lệ",
                    "Invalid signature"
                ));
            }
            
            // Bước 2: Lấy thông tin từ VNPay
            String vnpTxnRef = params.get("vnp_TxnRef"); // Order ID
            String vnpResponseCode = params.get("vnp_ResponseCode");
            String vnpTransactionStatus = params.get("vnp_TransactionStatus");
            
            // Bước 3: Kiểm tra kết quả thanh toán
            if ("00".equals(vnpResponseCode) && "00".equals(vnpTransactionStatus)) {
                log.info("✅ VNPay callback - Payment verified for order: {}", vnpTxnRef);
                
                // ✅ CẬP NHẬT INVENTORY - Giảm tồn kho khi thanh toán thành công
                try {
                    Long orderId = Long.parseLong(vnpTxnRef);
                    orderService.processSuccessfulPayment(orderId);
                    log.info("✅ Inventory updated successfully for order: {}", orderId);
                } catch (Exception e) {
                    log.error("❌ Error updating inventory for order: {}", vnpTxnRef, e);
                    return ResponseEntity.ok(ApiResponse.error(
                        "Lỗi cập nhật tồn kho",
                        e.getMessage()
                    ));
                }
                
                return ResponseEntity.ok(ApiResponse.success(
                    "Thanh toán thành công",
                    Map.of(
                        "orderId", vnpTxnRef,
                        "responseCode", vnpResponseCode,
                        "transactionStatus", vnpTransactionStatus
                    )
                ));
            } else {
                log.warn("❌ VNPay callback - Payment failed for order: {}. Code: {}", vnpTxnRef, vnpResponseCode);
                
                return ResponseEntity.ok(ApiResponse.error(
                    "Thanh toán thất bại",
                    "Response code: " + vnpResponseCode
                ));
            }
            
        } catch (Exception e) {
            log.error("❌ Error verifying VNPay callback: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("Lỗi xử lý callback", e.getMessage())
            );
        }
    }
    
    /**
     * MoMo IPN callback (tương tự VNPay)
     */
    @PostMapping("/momo/callback")
    public ResponseEntity<?> handleMoMoCallback(@RequestBody Map<String, Object> params) {
        try {
            log.info("📥 Received MoMo callback: {}", params);
            
            // TODO: Implement MoMo callback logic
            // Similar to VNPay but with MoMo's specific parameters
            
            return ResponseEntity.ok(ApiResponse.success("MoMo callback processed", params));
        } catch (Exception e) {
            log.error("Error processing MoMo callback: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi xử lý MoMo callback", e.getMessage()));
        }
    }
    
    /**
     * TEST ENDPOINT - Simulate VNPay payment success
     * Chỉ dùng trong development để test flow thanh toán
     * 
     * @param orderId ID của order cần test
     * @return Response
     */
    @PostMapping("/vnpay/test-success/{orderId}")
    public ResponseEntity<?> testVNPaySuccess(@PathVariable Long orderId) {
        try {
            log.info("🧪 TEST: Simulating VNPay payment success for order: {}", orderId);
            
            // Gọi trực tiếp logic xử lý thanh toán thành công
            orderService.processSuccessfulPayment(orderId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "Test payment successful - Inventory updated", 
                Map.of(
                    "orderId", orderId,
                    "message", "Đã giảm inventory thành công (TEST MODE)"
                )
            ));
        } catch (Exception e) {
            log.error("❌ Test payment error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("Test payment failed", e.getMessage())
            );
        }
    }
}
