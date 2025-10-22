package com.sneakery.store.service;

import com.sneakery.store.dto.WishlistDto;
import com.sneakery.store.entity.Product;
import com.sneakery.store.entity.User;
import com.sneakery.store.entity.Wishlist;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.ProductRepository;
import com.sneakery.store.repository.UserRepository;
import com.sneakery.store.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service: WishlistService
 * Quản lý danh sách yêu thích của user
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Lấy danh sách wishlist của user
     */
    @Transactional(readOnly = true)
    public List<WishlistDto> getWishlistByUserId(Long userId) {
        log.info("Fetching wishlist for user ID: {}", userId);
        
        List<Wishlist> wishlists = wishlistRepository.findByUserIdWithDetails(userId);
        
        return wishlists.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Thêm sản phẩm vào wishlist
     */
    @Transactional
    public WishlistDto addToWishlist(Long userId, Long productId) {
        log.info("Adding product {} to wishlist for user {}", productId, userId);
        
        // Kiểm tra user tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User không tồn tại"));
        
        // Kiểm tra product tồn tại và active
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại"));
        
        if (!product.getIsActive()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Sản phẩm không còn hoạt động");
        }
        
        // Kiểm tra đã tồn tại trong wishlist chưa
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new ApiException(HttpStatus.CONFLICT, "Sản phẩm đã có trong danh sách yêu thích");
        }
        
        // Tạo wishlist mới
        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();
        
        wishlist = wishlistRepository.save(wishlist);
        log.info("Added product {} to wishlist successfully", productId);
        
        return convertToDto(wishlist);
    }

    /**
     * Xóa sản phẩm khỏi wishlist
     */
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        log.info("Removing product {} from wishlist for user {}", productId, userId);
        
        // Kiểm tra tồn tại
        if (!wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Sản phẩm không có trong danh sách yêu thích");
        }
        
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
        log.info("Removed product {} from wishlist successfully", productId);
    }

    /**
     * Kiểm tra sản phẩm có trong wishlist không
     */
    @Transactional(readOnly = true)
    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    /**
     * Đếm số sản phẩm trong wishlist
     */
    @Transactional(readOnly = true)
    public long countWishlistItems(Long userId) {
        return wishlistRepository.countByUserId(userId);
    }

    /**
     * Xóa toàn bộ wishlist của user
     */
    @Transactional
    public void clearWishlist(Long userId) {
        log.info("Clearing wishlist for user {}", userId);
        
        List<Wishlist> wishlists = wishlistRepository.findByUserIdWithDetails(userId);
        wishlistRepository.deleteAll(wishlists);
        
        log.info("Cleared {} items from wishlist", wishlists.size());
    }

    /**
     * Convert Entity to DTO
     */
    private WishlistDto convertToDto(Wishlist wishlist) {
        Product product = wishlist.getProduct();
        
        // Lấy giá thấp nhất từ variants
        BigDecimal price = product.getVariants().stream()
                .filter(v -> v.getStockQuantity() > 0)
                .map(v -> v.getPriceSale() != null ? v.getPriceSale() : v.getPriceBase())
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        BigDecimal priceBase = product.getVariants().stream()
                .map(v -> v.getPriceBase())
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        BigDecimal priceSale = product.getVariants().stream()
                .filter(v -> v.getPriceSale() != null && v.getStockQuantity() > 0)
                .map(v -> v.getPriceSale())
                .min(BigDecimal::compareTo)
                .orElse(null);
        
        // Lấy hình ảnh đại diện
        String imageUrl = product.getVariants().stream()
                .filter(v -> v.getImageUrl() != null)
                .findFirst()
                .map(v -> v.getImageUrl())
                .orElse(null);
        
        return WishlistDto.builder()
                .id(wishlist.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productSlug(product.getSlug())
                .brandName(product.getBrand().getName())
                .imageUrl(imageUrl)
                .price(price)
                .priceBase(priceBase)
                .priceSale(priceSale)
                .isActive(product.getIsActive())
                .addedAt(wishlist.getCreatedAt())
                .build();
    }
}

