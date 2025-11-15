package com.sneakery.store.controller;

import com.sneakery.store.dto.CouponDto;
import com.sneakery.store.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class CouponPublicController {

    private final CouponService couponService;

    /**
     * Danh sách tất cả coupon đang hoạt động cho FE
     */
    @GetMapping("/active")
    public ResponseEntity<List<CouponDto>> getActiveCoupons() {
        return ResponseEntity.ok(couponService.getActiveCoupons());
    }

    /**
     * Validate mã coupon trước khi áp dụng
     */
    @GetMapping("/validate/{code}")
    public ResponseEntity<CouponDto> validate(@PathVariable String code) {
        return ResponseEntity.ok(couponService.validateCouponCode(code));
    }
}
