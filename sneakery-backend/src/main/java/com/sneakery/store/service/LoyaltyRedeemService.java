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

/**
 * Loyalty Redeem Service
 * Service xử lý logic trừ điểm loyalty
 *
 * ⭐ Chạy trong Transaction độc lập với Checkout (REQUIRES_NEW)
 * → Nếu redeem lỗi, vẫn giữ được Order (không rollback toàn bộ)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoyaltyRedeemService {

    private static final int VND_PER_POINT = 1000;

    private final LoyaltyPointRepository loyaltyPointRepository;
    private final UserRepository userRepository;

    /**
     * Redeem points trong giao dịch tách riêng
     *
     * @param userId user đang redeem
     * @param pointsToUse số điểm yêu cầu redeem
     * @param order đơn hàng chứa transaction redeem
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void redeemPoints(Long userId, int pointsToUse, Order order) {
        log.info("🎁 [REDEEM-NEW-TX] Starting redeem {} points for user {}", pointsToUse, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "User không tồn tại để redeem điểm"));

        if (pointsToUse <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Số điểm redeem phải > 0");
        }

        // 🎯 Create redeem log (negative points)
        LoyaltyPoint redeemLog = LoyaltyPoint.builder()
                .user(user)
                .points(-pointsToUse) // DB CHECK: Redeem phải là số âm
                .transactionType("redeem")
                .redeemedInOrder(order)
                .description("Đổi điểm cho đơn hàng " + order.getOrderNumber())
                .expiresAt(null) // DB CHECK: Redeem points không có hạn
                .build();

        loyaltyPointRepository.save(redeemLog);

        log.info("✅ [REDEEM-NEW-TX] User {} redeemed {} points ({}) VND for order {}",
                userId,
                pointsToUse,
                pointsToUse * VND_PER_POINT,
                order.getId()
        );
    }

    /**
     * Helper: Tính số tiền tương ứng từ điểm
     */
    public static long calculateDiscountFromPoints(int points) {
        return (long) points * VND_PER_POINT;
    }
}
