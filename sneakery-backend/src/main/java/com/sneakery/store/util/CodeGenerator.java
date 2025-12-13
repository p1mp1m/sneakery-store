package com.sneakery.store.util;

import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.Locale;

@Component
public class CodeGenerator {

    /**
     * Tạo mã sản phẩm theo brand, ví dụ:
     * NIKE → NIKE-001
     * ADIDAS → ADIDAS-015
     *
     * @param brandName tên thương hiệu (ví dụ: "Nike")
     * @param lastNumber số thứ tự cuối cùng đã tồn tại trong DB (null nếu chưa có)
     */
    public String generateProductCodeByBrand(String brandName, Integer lastNumber) {
        String brandCode = toBrandCode(brandName);
        int next = (lastNumber != null ? lastNumber + 1 : 1);
        return brandCode + "-" + formatNumber(next);
    }

    /**
     * Chuyển brand name thành code:
     * Nike → NIKE
     * New Balance → NEWBALANCE
     * Jordan → JORDAN
     */
    private String toBrandCode(String name) {
        if (name == null) return "UNKNOWN";

        // Xóa dấu tiếng Việt nếu có
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        // Xóa khoảng trắng
        normalized = normalized.replaceAll("\\s+", "");

        // Giới hạn tối đa 6 ký tự
        if (normalized.length() > 6) {
            normalized = normalized.substring(0, 6);
        }

        return normalized.toUpperCase(Locale.ROOT);
    }


    /**
     * Padding 3 số (001, 002, ... 999)
     */
    private String formatNumber(int number) {
        return new DecimalFormat("000").format(number);
    }
}
