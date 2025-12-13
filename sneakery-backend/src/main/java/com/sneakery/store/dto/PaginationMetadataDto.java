package com.sneakery.store.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO chứa metadata đầy đủ cho pagination response
 * Bổ sung thêm các thông tin: hasNext, hasPrevious, first, last
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Pagination metadata với thông tin đầy đủ")
public class PaginationMetadataDto {

    @Schema(description = "Số trang hiện tại (0-based)", example = "0")
    private int number;

    @Schema(description = "Số phần tử mỗi trang", example = "10")
    private int size;

    @Schema(description = "Tổng số phần tử", example = "100")
    private long totalElements;

    @Schema(description = "Tổng số trang", example = "10")
    private int totalPages;

    @Schema(description = "Có trang tiếp theo không", example = "true")
    private boolean hasNext;

    @Schema(description = "Có trang trước không", example = "false")
    private boolean hasPrevious;

    @Schema(description = "Có phải trang đầu tiên không", example = "true")
    private boolean first;

    @Schema(description = "Có phải trang cuối cùng không", example = "false")
    private boolean last;

    @Schema(description = "Số phần tử trong trang hiện tại", example = "10")
    private int numberOfElements;

    /**
     * Tạo PaginationMetadataDto từ Spring Data Page
     * 
     * @param page Spring Data Page object
     * @return PaginationMetadataDto
     */
    public static <T> PaginationMetadataDto fromPage(org.springframework.data.domain.Page<T> page) {
        return PaginationMetadataDto.builder()
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .first(page.isFirst())
                .last(page.isLast())
                .numberOfElements(page.getNumberOfElements())
                .build();
    }
}

