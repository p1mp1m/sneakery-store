package com.sneakery.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "Addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nhiều địa chỉ thuộc về 1 User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Cột 'user_id' trong bảng Addresses
    @JsonIgnore // Tránh vòng lặp JSON
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "line1", nullable = false)
    private String line1; // Địa chỉ (số nhà, tên đường...)

    @Column(name = "line2")
    private String line2; // (Tùy chọn, ví dụ: tên tòa nhà, số phòng)

    @Column(name = "city", nullable = false)
    private String city; // Thành phố / Tỉnh

    @Column(name = "district")
    private String district; // Quận / Huyện

    @Column(name = "province") // CSDL của bạn có cả 'city' và 'province'? 
    private String province; // Phường / Xã (hoặc Tỉnh/Thành nếu city là Quận/Huyện)
                             // Tùy bạn quy ước

    @Column(name = "postal_code")
    private String postalCode;
}