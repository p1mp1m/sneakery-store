package com.sneakery.store.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Wrapper DTO cho paginated response với metadata đầy đủ
 * Đảm bảo frontend nhận được tất cả thông tin pagination cần thiết
 * 
 * @param <T> Type của content items
 * @author Sneakery Store Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Paginated response với metadata đầy đủ")
public class PaginatedResponseDto<T> {

    @Schema(description = "Danh sách items trong trang hiện tại")
    private List<T> content;

    @Schema(description = "Pagination metadata")
    private PaginationMetadataDto pagination;

    /**
     * Tạo PaginatedResponseDto từ Spring Data Page
     * 
     * @param page Spring Data Page object
     * @return PaginatedResponseDto với content và pagination metadata
     */
    public static <T> PaginatedResponseDto<T> fromPage(Page<T> page) {
        return PaginatedResponseDto.<T>builder()
                .content(page.getContent())
                .pagination(PaginationMetadataDto.fromPage(page))
                .build();
    }

    /**
     * Tạo PaginatedResponseDto từ Spring Data Page với custom content mapping
     * 
     * @param page Spring Data Page object
     * @param contentMapper Function để map content items
     * @return PaginatedResponseDto với mapped content và pagination metadata
     */
    public static <T, R> PaginatedResponseDto<R> fromPage(
            Page<T> page,
            java.util.function.Function<T, R> contentMapper
    ) {
        List<R> mappedContent = page.getContent().stream()
                .map(contentMapper)
                .collect(java.util.stream.Collectors.toList());

        return PaginatedResponseDto.<R>builder()
                .content(mappedContent)
                .pagination(PaginationMetadataDto.fromPage(page))
                .build();
    }
}

