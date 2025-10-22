package com.sneakery.store.controller;

import com.sneakery.store.dto.SizeChartDto;
import com.sneakery.store.entity.SizeChart;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.SizeChartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller: SizeChartController (Public API)
 * API endpoints lấy bảng size giày
 */
@RestController
@RequestMapping("/api/size-charts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SizeChartController {

    private final SizeChartRepository sizeChartRepository;

    /**
     * Lấy size chart theo brandId và category (optional)
     * GET /api/size-charts?brandId=1&category=Running
     */
    @GetMapping
    public ResponseEntity<List<SizeChartDto>> getSizeCharts(
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false) String category
    ) {
        List<SizeChart> sizeCharts;
        
        if (brandId != null && category != null) {
            // Lấy theo brand và category
            sizeCharts = sizeChartRepository.findByBrandIdAndCategory(brandId, category);
        } else if (brandId != null) {
            // Lấy theo brand only
            sizeCharts = sizeChartRepository.findByBrandIdWithDetails(brandId);
        } else {
            // Lấy tất cả
            sizeCharts = sizeChartRepository.findAll();
        }
        
        List<SizeChartDto> dtos = sizeCharts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Lấy size chart theo ID
     * GET /api/size-charts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<SizeChartDto> getSizeChartById(@PathVariable Integer id) {
        SizeChart sizeChart = sizeChartRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Size chart không tồn tại"));
        
        return ResponseEntity.ok(convertToDto(sizeChart));
    }

    /**
     * Lấy danh sách categories theo brand
     * GET /api/size-charts/brands/{brandId}/categories
     */
    @GetMapping("/brands/{brandId}/categories")
    public ResponseEntity<List<String>> getCategoriesByBrand(@PathVariable Integer brandId) {
        List<String> categories = sizeChartRepository.findDistinctCategoriesByBrandId(brandId);
        return ResponseEntity.ok(categories);
    }

    /**
     * Convert Entity to DTO
     */
    private SizeChartDto convertToDto(SizeChart sizeChart) {
        return SizeChartDto.builder()
                .id(sizeChart.getId())
                .brandId(sizeChart.getBrand().getId())
                .brandName(sizeChart.getBrand().getName())
                .category(sizeChart.getCategory())
                .size(sizeChart.getSize())
                .sizeUs(sizeChart.getSizeUs())
                .sizeUk(sizeChart.getSizeUk())
                .lengthCm(sizeChart.getLengthCm())
                .widthCm(sizeChart.getWidthCm())
                .build();
    }
}

