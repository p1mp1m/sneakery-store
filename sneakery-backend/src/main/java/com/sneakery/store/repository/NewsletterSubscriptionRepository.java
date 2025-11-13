package com.sneakery.store.repository;

import com.sneakery.store.entity.NewsletterSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsletterSubscriptionRepository extends JpaRepository<NewsletterSubscription, Long> {
    
    /**
     * Tìm subscription theo email
     */
    Optional<NewsletterSubscription> findByEmail(String email);
    
    /**
     * Kiểm tra email đã đăng ký chưa
     */
    boolean existsByEmail(String email);
    
    /**
     * Kiểm tra email đã đăng ký và đang active
     */
    boolean existsByEmailAndIsActiveTrue(String email);
}




