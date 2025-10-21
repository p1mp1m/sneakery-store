package com.sneakery.store.repository;

import com.sneakery.store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
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

    /**
     * Kiểm tra nhanh xem email đã tồn tại trong CSDL chưa.
     * Tối ưu hơn việc dùng findByEmail().isPresent()
     *
     * @param email Email cần kiểm tra
     * @return true nếu email đã tồn tại, false nếu chưa
     */
    boolean existsByEmail(String email);
}