package com.sneakery.store.repository;

import com.sneakery.store.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Lấy tất cả sản phẩm và JOIN FETCH các biến thể liên quan.
     * Điều này giúp giải quyết lỗi LazyInitializationException và tối ưu hiệu năng.
     * "LEFT JOIN" đảm bảo sản phẩm vẫn được lấy ngay cả khi nó chưa có biến thể nào.
     * "DISTINCT" để tránh trả về các sản phẩm bị trùng lặp.
     */


    @Query("""
    SELECT DISTINCT p
    FROM Product p
    LEFT JOIN FETCH p.brand
    LEFT JOIN FETCH p.categories
    """)
List<Product> findAllWithBrandAndCategories();

@Query("""
    SELECT DISTINCT p
    FROM Product p
    LEFT JOIN FETCH p.brand
    LEFT JOIN FETCH p.categories
    WHERE p.id IN :ids
""")
List<Product> findByIdInWithBrandAndCategories(@Param("ids") List<Long> ids);

//✅ Cái đầu dùng cho lấy toàn bộ (không phân trang).
//✅ Cái thứ hai dùng khi bạn có Pageable.


    @Query("SELECT COALESCE(MAX(p.id), 0) FROM Product p")
    Long findMaxId();
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants")
    List<Product> findAllWithVariants();

    // Thay đổi phương thức để nhận Pageable và trả về Page
    @Query(value = "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Product p")
    Page<Product> findAllWithVariants(Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Product p " +
            "JOIN FETCH p.variants v " +
            "JOIN FETCH p.brand b",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Product p JOIN p.variants v")
    Page<Product> findAllWithDetails(Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.brand " +
            "LEFT JOIN FETCH p.categories " +
            "LEFT JOIN FETCH p.variants " +
            "WHERE p.id = :productId")
    Optional<Product> findByIdWithDetails(@Param("productId") Long productId);
}