package com.sneakery.store.service;

import com.sneakery.store.dto.NewsletterSubscriptionDto;
import com.sneakery.store.entity.NewsletterSubscription;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.NewsletterSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service quản lý Newsletter Subscriptions
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsletterService {

    private final NewsletterSubscriptionRepository newsletterRepository;
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Đăng ký nhận tin mới
     * 
     * @param email Email của người đăng ký
     * @return NewsletterSubscriptionDto của subscription vừa tạo
     * @throws ApiException nếu email không hợp lệ hoặc đã đăng ký
     */
    @Transactional
    public NewsletterSubscriptionDto subscribe(String email) {
        // Validate email format
        if (email == null || email.trim().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }
        
        email = email.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email không hợp lệ");
        }
        
        // Kiểm tra email đã đăng ký chưa
        Optional<NewsletterSubscription> existing = newsletterRepository.findByEmail(email);
        
        if (existing.isPresent()) {
            NewsletterSubscription subscription = existing.get();
            
            // Nếu đã đăng ký và đang active
            if (subscription.getIsActive()) {
                throw new ApiException(HttpStatus.CONFLICT, "Email này đã đăng ký nhận tin");
            }
            
            // Nếu đã đăng ký nhưng đã unsubscribe, reactivate
            subscription.setIsActive(true);
            subscription.setUnsubscribedAt(null);
            NewsletterSubscription updated = newsletterRepository.save(subscription);
            log.info("Reactivated newsletter subscription for email: {}", email);
            return convertToDto(updated);
        }
        
        // Tạo subscription mới
        NewsletterSubscription subscription = new NewsletterSubscription();
        subscription.setEmail(email);
        subscription.setIsActive(true);
        
        NewsletterSubscription saved = newsletterRepository.save(subscription);
        log.info("Created new newsletter subscription for email: {}", email);
        
        return convertToDto(saved);
    }

    /**
     * Hủy đăng ký nhận tin
     * 
     * @param email Email của người hủy đăng ký
     * @throws ApiException nếu email không tồn tại
     */
    @Transactional
    public void unsubscribe(String email) {
        NewsletterSubscription subscription = newsletterRepository.findByEmail(email)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Email này chưa đăng ký nhận tin"));
        
        subscription.setIsActive(false);
        subscription.setUnsubscribedAt(java.time.LocalDateTime.now());
        newsletterRepository.save(subscription);
        
        log.info("Unsubscribed newsletter for email: {}", email);
    }

    private NewsletterSubscriptionDto convertToDto(NewsletterSubscription subscription) {
        return NewsletterSubscriptionDto.builder()
            .id(subscription.getId())
            .email(subscription.getEmail())
            .isActive(subscription.getIsActive())
            .subscribedAt(subscription.getSubscribedAt())
            .build();
    }
}

