package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO quản lý biến thể sản phẩm dành cho Admin
 *
 * <p>Chứa đầy đủ thông tin chi tiết của một biến thể bao gồm:
 * <ul>
 *   <li>Giá gốc, giá sale, giá vốn</li>
 *   <li>Màu sắc, size</li>
 *   <li>Trạng thái kho và cảnh báo</li>
 *   <li>Thông tin liên kết với sản phẩm cha</li>
 *   <li>Các trường tính toán cho dashboard</li>
 * </ul>
 *
 * <p>Lưu ý: ảnh hiển thị sẽ được xử lý qua VariantImageDto,
 * do đó biến thể KHÔNG còn trường imageUrl.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Biến thể sản phẩm - dùng cho Admin")
public class AdminProductVariantDto {

    // ====== Basic Info ======
    @Schema(description = "ID của biến thể", example = "5")
    private Long id;

    @Schema(description = "SKU của biến thể", example = "SKU-000005")
    private String sku;

    @Schema(description = "Size giày", example = "40")
    private String size;

    @Schema(description = "Màu sắc của biến thể", example = "Black")
    private String color;

    // ====== Pricing ======
    @Schema(description = "Giá gốc", example = "42185000")
    private BigDecimal priceBase;

    @Schema(description = "Giá giảm", example = "36165000")
    private BigDecimal priceSale;

    @Schema(description = "Giá vốn (để tính lời lãi, chỉ hiển thị nội bộ)")
    private BigDecimal costPrice;

    // ====== Inventory ======
    @Schema(description = "Số lượng tồn kho", example = "50")
    private Integer stockQuantity;

    @Schema(description = "Ngưỡng cảnh báo hết hàng", example = "5")
    private Integer lowStockThreshold;

    @Schema(description = "Khối lượng đóng gói (grams)")
    private Integer weightGrams;

    // ❌ ĐÃ XOÁ imageUrl
    // private String imageUrl;

    // ====== Status ======
    @Schema(description = "Trạng thái hoạt động")
    private Boolean isActive;

    @Schema(description = "Ngày tạo bản ghi")
    private LocalDateTime createdAt;

    @Schema(description = "Ngày cập nhật bản ghi")
    private LocalDateTime updatedAt;

    // ====== Product Info (Parent Product) ======
    @Schema(description = "ID sản phẩm cha")
    private Long productId;

    @Schema(description = "Tên sản phẩm cha")
    private String productName;

    @Schema(description = "Slug sản phẩm cha")
    private String productSlug;

    @Schema(description = "Tên thương hiệu")
    private String brandName;

    // ====== Computed Fields ======
    @Schema(description = "Giá hiển thị hiện tại (format VND)", example = "36,165,000₫")
    private String currentPrice;

    @Schema(description = "Trạng thái kho hiển thị (Còn hàng / Hết hàng)")
    private String stockStatus;

    @Schema(description = "Cảnh báo gần hết hàng")
    private Boolean isLowStock;

    @Schema(description = "Đánh dấu hết hàng hoàn toàn")
    private Boolean isOutOfStock;
}
