package com.sneakery.store.repository;

import com.sneakery.store.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Spring Data JPA tự động tạo câu lệnh SELECT ... WHERE email = ?
     *
     * @param email Email cần tìm
     * @return Optional chứa User nếu tìm thấy
     */
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    /**
     * Kiểm tra nhanh xem email đã tồn tại trong CSDL chưa.
     * Tối ưu hơn việc dùng findByEmail().isPresent()
     *
     * @param email Email cần kiểm tra
     * @return true nếu email đã tồn tại, false nếu chưa
     */
    boolean existsByEmail(String email);

    /**
     * Tìm kiếm và lọc người dùng cho Admin với search, role, status
     * Search: tìm theo email, fullName, phoneNumber
     * Role: lọc theo vai trò (USER, ADMIN, MODERATOR)
     * Status: lọc theo trạng thái (active/inactive)
     */
    @Query("SELECT u FROM User u " +
            "WHERE (:search IS NULL OR :search = '' OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:role IS NULL OR :role = '' OR u.role = :role) " +
            "AND (:isActive IS NULL OR u.isActive = :isActive) " +
            "AND (u.deletedAt IS NULL)")
    Page<User> findAllWithFilters(
            @Param("search") String search,
            @Param("role") String role,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    /**
     * Đếm số lượng users được tạo trong khoảng thời gian
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    long countByCreatedAtBetween(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate
    );
}