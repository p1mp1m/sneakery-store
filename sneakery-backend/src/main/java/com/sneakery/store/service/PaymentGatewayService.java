package com.sneakery.store.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PaymentGatewayService {

    @Value("${payment.vnpay.url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpayUrl;

    @Value("${payment.momo.url:https://test-payment.momo.vn/gw_payment/transactionProcessor}")
    private String momoUrl;

    @Value("${payment.vnpay.return-url:http://localhost:5173/payment/callback}")
    private String returnUrl;

    public String createVNPayPaymentUrl(Long orderId, BigDecimal amount, String orderInfo) {
        log.info("Creating VNPay payment URL for order: {}", orderId);
        
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", "YOUR_TMN_CODE");
        params.put("vnp_Amount", String.valueOf(amount.multiply(BigDecimal.valueOf(100)).longValue()));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", orderId.toString());
        params.put("vnpramInfo", orderInfo);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", returnUrl);
        
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            queryString.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        
        return vnpayUrl + "?" + queryString.toString();
    }

    public String createMoMoPaymentUrl(Long orderId, BigDecimal amount, String orderInfo) {
        log.info("Creating MoMo payment URL for order: {}", orderId);
        
        // MoMo integration logic here
        // In production, implement actual MoMo API integration
        
        return momoUrl + "?orderId=" + orderId + "&amount=" + amount;
    }

    public boolean verifyPaymentCallback(String transactionId, Map<String, String> params) {
        log.info("Verifying payment callback: {}", transactionId);
        
        // Verify payment signature and status
        // In production, implement actual verification logic
        
        return true;
    }
}

