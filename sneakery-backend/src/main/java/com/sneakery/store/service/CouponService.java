package com.sneakery.store.service;

import com.sneakery.store.dto.CouponDto;
import com.sneakery.store.entity.Coupon;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service xử lý logic cho Coupon
 */
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    /**
     * Lấy tất cả coupon với filter
     */
    @Transactional(readOnly = true)
    public Page<CouponDto> getAllCoupons(String search, String type, String status, Pageable pageable) {
        // TODO: Implement filter logic
        Page<Coupon> coupons = couponRepository.findAll(pageable);
        return coupons.map(this::convertToDto);
    }

    /**
     * Lấy coupon theo ID
     */
    @Transactional(readOnly = true)
    public CouponDto getCouponById(Integer id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy coupon"));
        return convertToDto(coupon);
    }

    /**
     * Tạo coupon mới
     */
    @Transactional
    public CouponDto createCoupon(CouponDto dto) {
        // Kiểm tra code đã tồn tại chưa
        if (couponRepository.existsByCode(dto.getCode())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mã coupon đã tồn tại");
        }

        // Validate dates
        if (dto.getEndAt().isBefore(dto.getStartAt())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ngày kết thúc phải sau ngày bắt đầu");
        }

        Coupon coupon = convertToEntity(dto);
        coupon = couponRepository.save(coupon);
        return convertToDto(coupon);
    }

    /**
     * Cập nhật coupon
     */
    @Transactional
    public CouponDto updateCoupon(Integer id, CouponDto dto) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy coupon"));

        // Validate dates
        if (dto.getEndAt().isBefore(dto.getStartAt())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ngày kết thúc phải sau ngày bắt đầu");
        }

        // Update fields (không update code và usesCount)
        coupon.setDescription(dto.getDescription());
        coupon.setDiscountType(dto.getDiscountType());
        coupon.setValue(dto.getValue());
        coupon.setMaxDiscountAmount(dto.getMaxDiscountAmount());
        coupon.setMinOrderAmount(dto.getMinOrderAmount());
        coupon.setStartAt(dto.getStartAt());
        coupon.setEndAt(dto.getEndAt());
        coupon.setMaxUses(dto.getMaxUses());
        coupon.setMaxUsesPerUser(dto.getMaxUsesPerUser());
        coupon.setIsActive(dto.getIsActive());

        coupon = couponRepository.save(coupon);
        return convertToDto(coupon);
    }

    /**
     * Xóa coupon
     */
    @Transactional
    public void deleteCoupon(Integer id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy coupon"));
        
        // Kiểm tra coupon đã được sử dụng chưa
        if (coupon.getUsesCount() > 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Không thể xóa coupon đã được sử dụng");
        }

        couponRepository.delete(coupon);
    }

    /**
     * Bật/tắt trạng thái coupon
     */
    @Transactional
    public CouponDto toggleStatus(Integer id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy coupon"));
        
        coupon.setIsActive(!coupon.getIsActive());
        coupon = couponRepository.save(coupon);
        return convertToDto(coupon);
    }

    /**
     * Validate coupon code
     */
    @Transactional(readOnly = true)
    public CouponDto validateCouponCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Mã coupon không tồn tại"));

        LocalDateTime now = LocalDateTime.now();

        // Kiểm tra active
        if (!coupon.getIsActive()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mã coupon đã bị vô hiệu hóa");
        }

        // Kiểm tra thời gian
        if (now.isBefore(coupon.getStartAt())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mã coupon chưa có hiệu lực");
        }
        if (now.isAfter(coupon.getEndAt())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mã coupon đã hết hạn");
        }

        // Kiểm tra số lượt dùng
        if (coupon.getMaxUses() != null && coupon.getUsesCount() >= coupon.getMaxUses()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mã coupon đã hết lượt sử dụng");
        }

        return convertToDto(coupon);
    }

    /**
     * Convert Entity to DTO
     */
    private CouponDto convertToDto(Coupon coupon) {
        return CouponDto.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType())
                .value(coupon.getValue())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .minOrderAmount(coupon.getMinOrderAmount())
                .startAt(coupon.getStartAt())
                .endAt(coupon.getEndAt())
                .maxUses(coupon.getMaxUses())
                .usesCount(coupon.getUsesCount())
                .maxUsesPerUser(coupon.getMaxUsesPerUser())
                .isActive(coupon.getIsActive())
                .build();
    }

    /**
     * Convert DTO to Entity
     */
    private Coupon convertToEntity(CouponDto dto) {
        Coupon coupon = new Coupon();
        coupon.setCode(dto.getCode().toUpperCase()); // Uppercase code
        coupon.setDescription(dto.getDescription());
        coupon.setDiscountType(dto.getDiscountType());
        coupon.setValue(dto.getValue());
        coupon.setMaxDiscountAmount(dto.getMaxDiscountAmount());
        coupon.setMinOrderAmount(dto.getMinOrderAmount());
        coupon.setStartAt(dto.getStartAt());
        coupon.setEndAt(dto.getEndAt());
        coupon.setMaxUses(dto.getMaxUses());
        coupon.setUsesCount(0); // Khởi tạo = 0
        coupon.setMaxUsesPerUser(dto.getMaxUsesPerUser() != null ? dto.getMaxUsesPerUser() : 1);
        coupon.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return coupon;
    }
}

