package com.sneakery.store.controller;

import com.sneakery.store.dto.CouponDto;
import com.sneakery.store.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller quản lý mã giảm giá (Coupons) - Chỉ dành cho Admin
 */
@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới được truy cập
@CrossOrigin(origins = "http://localhost:5173")
public class AdminCouponController {

    private final CouponService couponService;

    /**
     * Lấy danh sách tất cả coupon với phân trang và filter
     */
    @GetMapping
    public ResponseEntity<Page<CouponDto>> getAllCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<CouponDto> coupons = couponService.getAllCoupons(search, type, status, pageable);
        return ResponseEntity.ok(coupons);
    }

    /**
     * Lấy chi tiết 1 coupon theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CouponDto> getCouponById(@PathVariable Integer id) {
        CouponDto coupon = couponService.getCouponById(id);
        return ResponseEntity.ok(coupon);
    }

    /**
     * Tạo coupon mới
     */
    @PostMapping
    public ResponseEntity<CouponDto> createCoupon(@Valid @RequestBody CouponDto couponDto) {
        CouponDto newCoupon = couponService.createCoupon(couponDto);
        return new ResponseEntity<>(newCoupon, HttpStatus.CREATED);
    }

    /**
     * Cập nhật coupon
     */
    @PutMapping("/{id}")
    public ResponseEntity<CouponDto> updateCoupon(
            @PathVariable Integer id,
            @Valid @RequestBody CouponDto couponDto
    ) {
        CouponDto updatedCoupon = couponService.updateCoupon(id, couponDto);
        return ResponseEntity.ok(updatedCoupon);
    }

    /**
     * Xóa coupon
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCoupon(@PathVariable Integer id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok("Đã xóa coupon thành công");
    }

    /**
     * Kích hoạt/vô hiệu hóa coupon
     */
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<CouponDto> toggleCouponStatus(@PathVariable Integer id) {
        CouponDto updatedCoupon = couponService.toggleStatus(id);
        return ResponseEntity.ok(updatedCoupon);
    }

    /**
     * Validate coupon code
     */
    @GetMapping("/validate/{code}")
    public ResponseEntity<CouponDto> validateCoupon(@PathVariable String code) {
        CouponDto coupon = couponService.validateCouponCode(code);
        return ResponseEntity.ok(coupon);
    }
}

