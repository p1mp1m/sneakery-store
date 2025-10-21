package com.sneakery.store.controller;

import com.sneakery.store.dto.AddressDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AddressController {

    private final AddressService addressService;

    /**
     * Lấy tất cả địa chỉ của user đang đăng nhập
     */
    @GetMapping
    public ResponseEntity<List<AddressDto>> getMyAddresses(
            @AuthenticationPrincipal User userPrincipal
    ) {
        List<AddressDto> addresses = addressService.getAddressesByUserId(userPrincipal.getId());
        return ResponseEntity.ok(addresses);
    }

    /**
     * Lấy 1 địa chỉ cụ thể
     */
    @GetMapping("/{id}")
    public ResponseEntity<AddressDto> getMyAddressById(
            @PathVariable Long id,
            @AuthenticationPrincipal User userPrincipal
    ) {
        AddressDto address = addressService.getAddressById(id, userPrincipal.getId());
        return ResponseEntity.ok(address);
    }

    /**
     * Tạo địa chỉ mới
     */
    @PostMapping
    public ResponseEntity<AddressDto> createMyAddress(
            @Valid @RequestBody AddressDto addressDto,
            @AuthenticationPrincipal User userPrincipal
    ) {
        AddressDto newAddress = addressService.createAddress(addressDto, userPrincipal.getId());
        return new ResponseEntity<>(newAddress, HttpStatus.CREATED);
    }

    /**
     * Cập nhật địa chỉ
     */
    @PutMapping("/{id}")
    public ResponseEntity<AddressDto> updateMyAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressDto addressDto,
            @AuthenticationPrincipal User userPrincipal
    ) {
        AddressDto updatedAddress = addressService.updateAddress(id, addressDto, userPrincipal.getId());
        return ResponseEntity.ok(updatedAddress);
    }

    /**
     * Xóa địa chỉ
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal User userPrincipal
    ) {
        addressService.deleteAddress(id, userPrincipal.getId());
        return ResponseEntity.noContent().build(); // Trả về 204 No Content
    }
}