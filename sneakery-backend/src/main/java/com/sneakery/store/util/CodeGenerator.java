package com.sneakery.store.util;

import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Bộ sinh mã tự động có thể tái sử dụng.
 * Hỗ trợ tiền tố (prefix), padding, và đồng bộ luồng.
 */
@Component
public class CodeGenerator {

    // Tăng tự động trong runtime (hoặc lấy từ DB)
    private final AtomicLong counter = new AtomicLong(0);

    /**
     * Sinh mã sản phẩm mới theo dạng "SP00001", "SP00002", ...
     *
     * @param lastId giá trị id cuối cùng (lấy từ DB)
     * @return mã sản phẩm mới
     */
    public String generateProductCode(Long lastId) {
        long next = (lastId != null ? lastId + 1 : counter.incrementAndGet());
        return formatCode("SP", next);
    }

    /**
     * Sinh mã đơn hàng "ORD00001", "ORD00002", ...
     */
    public String generateOrderCode(Long lastId) {
        long next = (lastId != null ? lastId + 1 : counter.incrementAndGet());
        return formatCode("ORD", next);
    }

    /**
     * Hàm định dạng chung: prefix + số có padding 5 chữ số
     */
    private String formatCode(String prefix, long number) {
        DecimalFormat df = new DecimalFormat("00000");
        return prefix + df.format(number);
    }
}

