package com.sneakery.store.repository;

import com.sneakery.store.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
    // JpaRepository đã cung cấp đủ các hàm cơ bản
    // Tạm thời chưa cần thêm hàm custom
}