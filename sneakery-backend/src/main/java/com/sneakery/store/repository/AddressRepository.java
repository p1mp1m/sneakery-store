package com.sneakery.store.repository;

import com.sneakery.store.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    // Tìm tất cả địa chỉ của một User
    List<Address> findByUserId(Long userId);

    // Tìm một địa chỉ cụ thể của một User (để bảo mật)
    Optional<Address> findByIdAndUserId(Long addressId, Long userId);

    // Tìm địa chỉ POS (cửa hàng) - dùng chung cho tất cả POS orders
    // Tìm theo line1 và address_type để đảm bảo tìm đúng địa chỉ POS
    Optional<Address> findByLine1AndAddressType(String line1, String addressType);
}