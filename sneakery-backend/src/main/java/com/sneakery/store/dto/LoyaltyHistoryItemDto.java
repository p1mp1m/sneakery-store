package com.sneakery.store.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO: LoyaltyHistoryItemDto
 * Response DTO cho /api/loyalty/history
 *
 * Mục tiêu:
 * - Trả về đúng các field FE đang dùng (id, points, transactionType, description, createdAt)
 * - Tránh trả Entity để không dính Hibernate proxy (ByteBuddyInterceptor)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "LoyaltyHistoryItemDto", description = "Item lịch sử điểm thưởng")
public class LoyaltyHistoryItemDto {

    @Schema(description = "ID bản ghi", example = "1")
    private Long id;

    @Schema(description = "Số điểm thay đổi (+/-). redeem thường âm", example = "-50")
    private Integer points;

    @Schema(description = "Loại giao dịch (earn/redeem/expire)", example = "redeem")
    private String transactionType;

    @Schema(description = "Mô tả", example = "Đổi điểm cho đơn hàng POS-20260112-0001")
    private String description;

    @Schema(description = "Thời điểm phát sinh giao dịch")
    private LocalDateTime createdAt;

    @Schema(description = "Thời điểm hết hạn (earn thường có, redeem thường null)")
    private LocalDateTime expiresAt;
}
