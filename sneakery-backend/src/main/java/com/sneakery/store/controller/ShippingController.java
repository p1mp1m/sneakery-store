package com.sneakery.store.controller;

import com.sneakery.store.dto.ShippingAddressRequestDto;
import com.sneakery.store.service.ShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ShippingController {

    private final ShippingService shippingService;

    @PostMapping("/calculate")
    public FeeResponse calculate(@RequestBody ShippingAddressRequestDto dto) {
        log.info("🚚 FE yêu cầu tính phí ship cho địa chỉ: {}", dto);

        double fee = shippingService.calculateShippingFee(dto);

        return new FeeResponse(fee);
    }

    record FeeResponse(double fee) {}
}
