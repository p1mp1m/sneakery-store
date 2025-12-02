package com.sneakery.store.service;

import com.sneakery.store.dto.ShippingAddressRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingService {

    private final GeocodingService geocodingService;
    private final DistanceService distanceService;

    // Địa chỉ cửa hàng mặc định
    private static final String STORE_ADDRESS =
            "23 Đường Cầu Giấy, Dịch Vọng Hậu, Cầu Giấy, Hà Nội, 100000";

    /**
     * Tính phí ship dựa trên khoảng cách.
     * Rule:
     *  - ≤ 5 km  → 15.000đ
     *  - ≤ 7 km  → 20.000đ
     *  - ≤ 15 km → 30.000đ
     *  - ≤ 30 km → 45.000đ
     *  - > 30 km → 70.000đ
     */
    public int calculateFee(double km) {
        if (km <= 5) return 15000;
        if (km <= 7) return 20000;
        if (km <= 15) return 30000;
        if (km <= 30) return 45000;
        return 70000;
    }

    public double calculateShippingFee(ShippingAddressRequestDto dto) {

        // =========================
        // 1. Geocode cửa hàng
        // =========================
        double[] storeCoords = geocodingService.geocode(STORE_ADDRESS);

        // =========================
        // 2. Geocode khách với fallback nhiều cấp
        // =========================
        double[] customerCoords = geocodingService.geocode(
                dto.getWard() + ", " + dto.getDistrict() + ", " + dto.getCity()
        );

        // Fallback cấp 1 → district + city
        if (customerCoords[0] == 0 && customerCoords[1] == 0) {
            log.warn("⚠️ Fallback 1: Không tìm thấy phường → thử quận...");
            customerCoords = geocodingService.geocode(
                    dto.getDistrict() + ", " + dto.getCity()
            );
        }

        // Fallback cấp 2 → city
        if (customerCoords[0] == 0 && customerCoords[1] == 0) {
            log.warn("⚠️ Fallback 2: Không tìm thấy quận → thử thành phố...");
            customerCoords = geocodingService.geocode(dto.getCity());
        }

        log.info("📌 Toạ độ khách (đã fallback): lat={}, lon={}", customerCoords[0], customerCoords[1]);

        // =========================
        // 3. Khoảng cách (km)
        // =========================
        double distanceKm = distanceService.calculateDistanceKm(
                storeCoords[0], storeCoords[1],
                customerCoords[0], customerCoords[1]
        );

        log.info("📦 Khoảng cách cửa hàng ➝ khách = {} km", distanceKm);

        // =========================
        // 4. ÁP DỤNG RULE TÍNH PHÍ SHIP
        // =========================
        int fee = calculateFee(distanceKm);

        log.info("💰 Phí ship áp dụng = {}đ (theo rule)", fee);

        return fee;
    }
}
