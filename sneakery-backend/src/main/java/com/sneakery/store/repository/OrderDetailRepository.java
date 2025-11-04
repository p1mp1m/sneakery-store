package com.sneakery.store.repository;

import com.sneakery.store.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    
    /**
     * Lấy top products bán chạy nhất (theo số lượng đã bán)
     */
    @Query(value = "SELECT TOP 5 " +
            "od.product_name AS productName, " +
            "SUM(od.quantity) AS totalSold " +
            "FROM Order_Details od " +
            "JOIN Orders o ON od.order_id = o.id " +
            "WHERE o.status IN ('delivered', 'completed', 'shipped', 'Delivered', 'Completed', 'Shipped') " +
            "GROUP BY od.product_name " +
            "ORDER BY totalSold DESC",
            nativeQuery = true)
    List<Object[]> getTopSellingProducts();
}