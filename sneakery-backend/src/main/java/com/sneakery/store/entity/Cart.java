package com.sneakery.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "Carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ với User (Một Cart thuộc về một User)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true) // Cột 'user_id' trong bảng Carts
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;
    
    // CSDL của bạn có cột này nhưng không dùng cho user đã đăng nhập
    @Column(name = "session_id")
    private String sessionId; 

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // V3.1 - expires_at for guest carts cleanup
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Quan hệ với CartItems (Một Cart có nhiều CartItem)
    @OneToMany(
        mappedBy = "cart",
        cascade = CascadeType.ALL, // Xóa Cart -> xóa hết Item
        orphanRemoval = true // Xóa Item khỏi List -> xóa Item khỏi CSDL
    )
    private List<CartItem> items = new ArrayList<>();
    
    // Helper method để đồng bộ 2 chiều
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }
}