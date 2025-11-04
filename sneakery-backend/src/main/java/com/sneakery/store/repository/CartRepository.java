package com.sneakery.store.repository;

import com.sneakery.store.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Tìm giỏ hàng theo User ID
    Optional<Cart> findByUserId(Long userId);

    // Tối ưu: Tìm giỏ hàng VÀ tải luôn Items + Variant + Product + Brand
    @Query("SELECT c FROM Cart c " +
           "LEFT JOIN FETCH c.items ci " +
           "LEFT JOIN FETCH ci.variant v " +
           "LEFT JOIN FETCH v.product p " +
           "LEFT JOIN FETCH p.brand " +
           "WHERE c.user.id = :userId")
    Optional<Cart> findByUserIdWithDetails(Long userId);

    // Tìm giỏ hàng theo Session ID (cho guest)
    Optional<Cart> findBySessionId(String sessionId);

    // Tối ưu: Tìm guest cart VÀ tải luôn Items + Variant + Product + Brand
    @Query("SELECT c FROM Cart c " +
           "LEFT JOIN FETCH c.items ci " +
           "LEFT JOIN FETCH ci.variant v " +
           "LEFT JOIN FETCH v.product p " +
           "LEFT JOIN FETCH p.brand " +
           "WHERE c.sessionId = :sessionId AND c.user IS NULL")
    Optional<Cart> findBySessionIdWithDetails(String sessionId);
}