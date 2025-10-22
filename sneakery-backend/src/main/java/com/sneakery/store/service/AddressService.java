package com.sneakery.store.service;

import com.sneakery.store.dto.AddressDto;
import com.sneakery.store.entity.Address;
import com.sneakery.store.entity.User;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.AddressRepository;
import com.sneakery.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    /**
     * Lấy tất cả địa chỉ của user
     */
    @Transactional(readOnly = true)
    public List<AddressDto> getAddressesByUserId(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        return addresses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Lấy 1 địa chỉ theo ID (có kiểm tra chủ sở hữu)
     */
    @Transactional(readOnly = true)
    public AddressDto getAddressById(Long addressId, Long userId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ"));
        return convertToDto(address);
    }

    /**
     * Tạo địa chỉ mới
     */
    @Transactional
    public AddressDto createAddress(AddressDto addressDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy user"));
        
        Address address = convertToEntity(addressDto);
        address.setUser(user);
        
        Address savedAddress = addressRepository.save(address);
        return convertToDto(savedAddress);
    }

    /**
     * Cập nhật địa chỉ
     */
    @Transactional
    public AddressDto updateAddress(Long addressId, AddressDto addressDto, Long userId) {
        // Kiểm tra xem địa chỉ này có thuộc user này không
        Address existingAddress = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ"));

        // Cập nhật các trường
        existingAddress.setRecipientName(addressDto.getRecipientName());
        existingAddress.setPhone(addressDto.getPhone());
        existingAddress.setLine1(addressDto.getLine1());
        existingAddress.setLine2(addressDto.getLine2());
        existingAddress.setCity(addressDto.getCity());
        existingAddress.setDistrict(addressDto.getDistrict());
        existingAddress.setWard(addressDto.getWard());
        existingAddress.setPostalCode(addressDto.getPostalCode());
        
        Address updatedAddress = addressRepository.save(existingAddress);
        return convertToDto(updatedAddress);
    }

    /**
     * Xóa địa chỉ
     */
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ"));
        
        addressRepository.delete(address);
    }

    // =================================================================
    // HÀM HELPER (MAPPER)
    // =================================================================

    private AddressDto convertToDto(Address address) {
        return AddressDto.builder()
                .id(address.getId())
                .recipientName(address.getRecipientName())
                .phone(address.getPhone())
                .line1(address.getLine1())
                .line2(address.getLine2())
                .city(address.getCity())
                .district(address.getDistrict())
                .ward(address.getWard())
                .postalCode(address.getPostalCode())
                .build();
    }

    private Address convertToEntity(AddressDto dto) {
        Address address = new Address();
        address.setRecipientName(dto.getRecipientName());
        address.setPhone(dto.getPhone());
        address.setLine1(dto.getLine1());
        address.setLine2(dto.getLine2());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setWard(dto.getWard());
        address.setPostalCode(dto.getPostalCode());
        return address;
    }
}