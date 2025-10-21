package com.sneakery.store.repository;

import com.sneakery.store.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    // JpaRepository đã cung cấp đủ các hàm cơ bản (save, find, delete...)
    // Tạm thời chưa cần thêm hàm custom
}