package com.sneakery.store.service;

import com.sneakery.store.entity.LoyaltyPoint;
import com.sneakery.store.entity.Order;
import com.sneakery.store.entity.User;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.LoyaltyPointRepository;
import com.sneakery.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Loyalty Points Service
 * Quản lý hệ thống tích điểm thưởng
 * Quy tắc: 1 điểm = 1,000 VND
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoyaltyService {

    private final LoyaltyPointRepository loyaltyPointRepository;
    private final UserRepository userRepository;

    // 1 point = 1,000 VND
    private static final int VND_PER_POINT = 1000;

    /**
     * Lấy balance điểm của user
     */
    @Transactional(readOnly = true)
    public int getUserPointsBalance(Long userId) {
        log.info("Fetching points balance for user ID: {}", userId);
        
        LocalDateTime now = LocalDateTime.now();
        List<LoyaltyPoint> points = loyaltyPointRepository.findByUserIdAndExpiresAtAfter(userId, now);
        
        int balance = points.stream()
                .mapToInt(lp -> {
                    if ("earn".equals(lp.getTransactionType())) {
                        return lp.getPoints();
                    } else if ("redeem".equals(lp.getTransactionType())) {
                        return -lp.getPoints();
                    }
                    return 0;
                })
                .sum();
        
        log.info("User {} has {} points", userId, balance);
        return Math.max(balance, 0); // Không cho âm
    }

    /**
     * Lấy transaction history
     */
    @Transactional(readOnly = true)
    public List<LoyaltyPoint> getUserPointsHistory(Long userId) {
        return loyaltyPointRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Earn points từ order
     * Tự động gọi khi order hoàn thành (delivered)
     */
    @Transactional
    public void earnPointsFromOrder(Order order) {
        log.info("💎 Earning points for order ID: {}", order.getId());
        
        // Tính points: 1 point = 1,000 VND
        int points = calculatePointsFromAmount(order.getTotalAmount());
        
        if (points <= 0) {
            log.info("Order amount too low to earn points");
            return;
        }
        
        LoyaltyPoint loyaltyPoint = new LoyaltyPoint();
        loyaltyPoint.setUser(order.getUser());
        loyaltyPoint.setPoints(points);
        loyaltyPoint.setTransactionType("earn");
        loyaltyPoint.setEarnedFromOrderId(order.getId());
        loyaltyPoint.setDescription(String.format("Tích điểm từ đơn hàng %s", order.getOrderNumber()));
        loyaltyPoint.setExpiresAt(LocalDateTime.now().plusYears(1)); // Hết hạn sau 1 năm
        
        loyaltyPointRepository.save(loyaltyPoint);
        
        // Update order
        order.setPointsEarned(points);
        
        log.info("✅ User {} earned {} points from order {}", 
            order.getUser().getId(), points, order.getId());
    }

    /**
     * Redeem points at checkout
     */
    @Transactional
    public BigDecimal redeemPoints(Long userId, int pointsToUse, Order order) {
        log.info("🎁 Redeeming {} points for user {}", pointsToUse, userId);
        
        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User không tồn tại"));
        
        // Check balance
        int currentBalance = getUserPointsBalance(userId);
        
        if (pointsToUse > currentBalance) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 
                String.format("Không đủ điểm. Số dư: %d, yêu cầu: %d", currentBalance, pointsToUse));
        }
        
        if (pointsToUse <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Số điểm phải lớn hơn 0");
        }
        
        // Tính discount amount
        BigDecimal discountAmount = BigDecimal.valueOf(pointsToUse * VND_PER_POINT);
        
        // Validate không vượt quá total amount
        if (discountAmount.compareTo(order.getTotalAmount()) > 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Số điểm sử dụng vượt quá giá trị đơn hàng");
        }
        
        // Tạo redemption record
        LoyaltyPoint redemption = new LoyaltyPoint();
        redemption.setUser(user);
        redemption.setPoints(pointsToUse);
        redemption.setTransactionType("redeem");
        redemption.setRedeemedInOrderId(order.getId());
        redemption.setDescription(String.format("Đổi điểm cho đơn hàng %s", order.getOrderNumber()));
        
        loyaltyPointRepository.save(redemption);
        
        // Update order
        order.setPointsUsed(pointsToUse);
        
        log.info("✅ User {} redeemed {} points = {} VND discount", 
            userId, pointsToUse, discountAmount);
        
        return discountAmount;
    }

    /**
     * Award bonus points (Admin)
     */
    @Transactional
    public void awardBonusPoints(Long userId, int points, String reason) {
        log.info("🎉 Awarding {} bonus points to user {}", points, userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User không tồn tại"));
        
        LoyaltyPoint bonus = new LoyaltyPoint();
        bonus.setUser(user);
        bonus.setPoints(points);
        bonus.setTransactionType("earn");
        bonus.setDescription(reason != null ? reason : "Điểm thưởng từ quản trị viên");
        bonus.setExpiresAt(LocalDateTime.now().plusYears(1));
        
        loyaltyPointRepository.save(bonus);
        
        log.info("✅ Awarded {} bonus points to user {}", points, userId);
    }

    /**
     * Calculate points from order amount
     */
    private int calculatePointsFromAmount(BigDecimal amount) {
        if (amount == null) {
            return 0;
        }
        
        // 1 point = 1,000 VND
        return amount.divide(BigDecimal.valueOf(VND_PER_POINT), 0, java.math.RoundingMode.DOWN).intValue();
    }

    /**
     * Calculate VND from points
     */
    public static BigDecimal calculateVndFromPoints(int points) {
        return BigDecimal.valueOf(points * VND_PER_POINT);
    }
}

