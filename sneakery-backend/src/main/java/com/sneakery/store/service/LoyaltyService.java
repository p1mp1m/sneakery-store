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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
        log.info("Fetching balance for user {}", userId);

        Integer balance = loyaltyPointRepository.calculateCurrentPoints(userId, LocalDateTime.now());
        int safeBalance = balance != null ? balance : 0;

        return Math.max(safeBalance, 0); // Không cho âm
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
        log.info("🎯 Earn points for order {}", order.getId());

        BigDecimal subtotal = order.getSubtotal() != null ? order.getSubtotal() : BigDecimal.ZERO;
        BigDecimal discountAmount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;

        BigDecimal pointsDiscount = BigDecimal.ZERO;
        if (order.getPointsUsed() != null && order.getPointsUsed() > 0) {
            pointsDiscount = BigDecimal.valueOf(order.getPointsUsed() * (long) VND_PER_POINT);
        }

        BigDecimal taxable = subtotal.subtract(discountAmount).subtract(pointsDiscount);
        if (taxable.compareTo(BigDecimal.ZERO) < 0) taxable = BigDecimal.ZERO;

        int points = taxable
                .divide(BigDecimal.valueOf(10000), 0, java.math.RoundingMode.HALF_UP)
                .intValue();

        if (points <= 0) {
            log.info("⛔ Taxable too low → no points");
            return;
        }

        LoyaltyPoint lp = new LoyaltyPoint();
        lp.setUser(order.getUser());
        lp.setPoints(points);
        lp.setTransactionType("earn");
        lp.setEarnedFromOrder(order);
        lp.setDescription("Tích điểm từ đơn hàng " + order.getOrderNumber());
        lp.setExpiresAt(LocalDateTime.now().plusYears(1));

        loyaltyPointRepository.save(lp);
        order.setPointsEarned(points);

        log.info("🏆 User {} earned {} points on taxable {}",
                order.getUser().getId(), points, taxable);
    }

    /**
     * Redeem points at checkout
     */
    @Transactional
    public BigDecimal redeemPoints(Long userId, int pointsToUse, Order order) {
        log.info("🎁 Redeeming {} points for user {}", pointsToUse, userId);

        // Validate user
        User user = userRepository.findById(Objects.requireNonNull(userId))
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

        // Convert to discount
        BigDecimal discountAmount = BigDecimal.valueOf(pointsToUse * VND_PER_POINT);

        // Tạo record REDEEM chuẩn DB CHECK
        LoyaltyPoint redemption = new LoyaltyPoint();
        redemption.setUser(user);

        // ⭐ DB CHECK: redeem phải là điểm âm
        redemption.setPoints(-pointsToUse);

        redemption.setTransactionType("redeem");
        redemption.setRedeemedInOrder(order);
        redemption.setDescription("Đổi điểm cho đơn hàng " + order.getOrderNumber());

        // ⭐ DB CHECK: redeem phải expiresAt = NULL
        redemption.setExpiresAt(null);

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
        
        User user = userRepository.findById(Objects.requireNonNull(userId))
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void redeemPointsInNewTx(Long userId, int pointsToUse, Order order) {
        redeemPoints(userId, pointsToUse, order);
    }

    /**
     * Calculate points from order amount
     */
    private int calculatePointsFromAmount(BigDecimal amount) {
        if (amount == null) {
            return 0;
        }

        // ⭐ Công thức mới: tổng tiền / 10,000 và làm tròn
        return amount
                .divide(BigDecimal.valueOf(10000), 0, java.math.RoundingMode.HALF_UP)
                .intValue();
    }

    /**
     * Calculate VND from points
     */
    public static BigDecimal calculateVndFromPoints(int points) {
        return BigDecimal.valueOf(points * VND_PER_POINT);
    }
}

