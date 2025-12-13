package com.sneakery.store.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class PaymentGatewayService {

    @Value("${payment.vnpay.url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpayUrl;

    @Value("${payment.vnpay.tmn-code:}")
    private String vnpayTmnCode;

    @Value("${payment.vnpay.hash-secret:}")
    private String vnpayHashSecret;

    @Value("${payment.vnpay.return-url:http://localhost:5173/payment/callback}")
    private String vnpayReturnUrl;

    @Value("${payment.vnpay.ipn-url:http://localhost:8080/api/payment/vnpay/ipn}")
    private String vnpayIpnUrl;

    @Value("${payment.momo.url:https://test-payment.momo.vn/gw_payment/transactionProcessor}")
    private String momoUrl;

    /**
     * Tạo URL thanh toán VNPay theo tài liệu chính thức
     * 
     * @param orderId ID đơn hàng
     * @param amount Số tiền thanh toán
     * @param orderInfo Thông tin đơn hàng
     * @param ipAddress IP của khách hàng
     * @return URL thanh toán VNPay
     */
    public String createVNPayPaymentUrl(Long orderId, BigDecimal amount, String orderInfo, String ipAddress) {
        try {
            log.info("🔐 Creating VNPay payment URL for order: {}, amount: {}", orderId, amount);
            
            // Tạo các tham số theo thứ tự alphabet để tạo secure hash
            Map<String, String> vnpParams = new TreeMap<>();
            
            // Required parameters theo VNPay documentation
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", vnpayTmnCode);
            vnpParams.put("vnp_Amount", String.valueOf(amount.multiply(BigDecimal.valueOf(100)).longValue())); // Nhân 100 theo tài liệu
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", orderId.toString()); // Mã đơn hàng
            vnpParams.put("vnp_OrderInfo", orderInfo); // Thông tin đơn hàng (không dấu)
            vnpParams.put("vnp_OrderType", "other"); // Loại hàng hóa
            vnpParams.put("vnp_Locale", "vn"); // Ngôn ngữ (vn hoặc en)
            vnpParams.put("vnp_ReturnUrl", vnpayReturnUrl); // URL return về frontend
            vnpParams.put("vnp_IpAddr", ipAddress != null ? ipAddress : "127.0.0.1");
            
            // Thời gian tạo giao dịch
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+7"));
            String vnpCreateDate = formatter.format(new Date());
            vnpParams.put("vnp_CreateDate", vnpCreateDate);
            
            // Thời gian hết hạn (15 phút)
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
            calendar.add(Calendar.MINUTE, 15);
            String vnpExpireDate = formatter.format(calendar.getTime());
            vnpParams.put("vnp_ExpireDate", vnpExpireDate);
            
            // Build query string và tạo secure hash
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                    query.append('&');
                }
                hashData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII.toString()));
                hashData.append('=');
                hashData.append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII.toString()));
                
                query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII.toString()));
            }
            
            // Tạo secure hash bằng HMAC SHA512
            String vnpSecureHash = hmacSHA512(vnpayHashSecret, hashData.toString());
            query.append("&vnp_SecureHash=").append(vnpSecureHash);
            
            String paymentUrl = vnpayUrl + "?" + query.toString();
            log.info("✅ VNPay payment URL created successfully");
            
            return paymentUrl;
            
        } catch (Exception e) {
            log.error("❌ Error creating VNPay payment URL: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create VNPay payment URL", e);
        }
    }

    /**
     * Xác thực chữ ký từ VNPay callback
     * 
     * @param params Parameters từ VNPay
     * @return true nếu chữ ký hợp lệ
     */
    public boolean verifyVNPayCallback(Map<String, String> params) {
        try {
            String vnpSecureHash = params.get("vnp_SecureHash");
            if (vnpSecureHash == null) {
                log.warn("❌ Missing vnp_SecureHash in callback");
                return false;
            }
            
            // Remove hash parameters
            Map<String, String> fields = new TreeMap<>(params);
            fields.remove("vnp_SecureHash");
            fields.remove("vnp_SecureHashType");
            
            // Build hash data
            StringBuilder hashData = new StringBuilder();
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                hashData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII.toString()));
                hashData.append('=');
                hashData.append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII.toString()));
            }
            
            String calculatedHash = hmacSHA512(vnpayHashSecret, hashData.toString());
            boolean isValid = calculatedHash.equalsIgnoreCase(vnpSecureHash);
            
            if (isValid) {
                log.info("✅ VNPay callback signature verified successfully");
            } else {
                log.warn("❌ VNPay callback signature verification failed");
            }
            
            return isValid;
            
        } catch (Exception e) {
            log.error("❌ Error verifying VNPay callback: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Tạo HMAC SHA512 hash
     */
    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] hash = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder result = new StringBuilder();
            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating HMAC SHA512", e);
        }
    }

    public String createMoMoPaymentUrl(Long orderId, BigDecimal amount, String orderInfo) {
        log.info("Creating MoMo payment URL for order: {}", orderId);
        
        // MoMo integration logic here
        // In production, implement actual MoMo API integration
        
        return momoUrl + "?orderId=" + orderId + "&amount=" + amount;
    }
}

