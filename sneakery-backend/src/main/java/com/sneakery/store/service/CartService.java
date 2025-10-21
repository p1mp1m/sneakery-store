package com.sneakery.store.service;

import com.sneakery.store.dto.AddToCartRequestDto;
import com.sneakery.store.dto.CartDto;
import com.sneakery.store.dto.CartItemDto;
import com.sneakery.store.entity.Cart;
import com.sneakery.store.entity.CartItem;
import com.sneakery.store.entity.ProductVariant;
import com.sneakery.store.entity.User;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.CartRepository;
import com.sneakery.store.repository.ProductVariantRepository;
import com.sneakery.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository variantRepository;

    /**
     * API 1: Lấy giỏ hàng của user
     */
    @Transactional(readOnly = true)
    public CartDto getCartByUserId(Long userId) {
        Cart cart = getOrCreateCart(userId);
        // Dùng query đã tối ưu
        cart = cartRepository.findByUserIdWithDetails(userId).orElse(cart); 
        return convertToCartDto(cart);
    }

    /**
     * API 2: Thêm/Cập nhật sản phẩm vào giỏ
     */
    @Transactional
    public CartDto addItemToCart(Long userId, AddToCartRequestDto requestDto) {
        Cart cart = getOrCreateCart(userId);
        
        // Tìm biến thể sản phẩm
        ProductVariant variant = variantRepository.findById(requestDto.getVariantId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm (variant)"));

        // Kiểm tra số lượng tồn kho
        if (variant.getStockQuantity() < requestDto.getQuantity()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Không đủ hàng tồn kho");
        }

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getVariant().getId().equals(requestDto.getVariantId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Nếu đã có -> Cập nhật số lượng
            CartItem item = existingItem.get();
            item.setQuantity(requestDto.getQuantity());
        } else {
            // Nếu chưa có -> Thêm mới
            CartItem newItem = new CartItem();
            newItem.setVariant(variant);
            newItem.setQuantity(requestDto.getQuantity());
            cart.addItem(newItem); // Dùng helper method
        }

        cartRepository.save(cart);
        // Tải lại chi tiết để trả về
        return getCartByUserId(userId);
    }

    /**
     * API 3: Xóa sản phẩm khỏi giỏ
     */
    @Transactional
    public CartDto removeItemFromCart(Long userId, Long variantId) {
        Cart cart = getOrCreateCart(userId);

        // Tìm item trong giỏ
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getVariant().getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Sản phẩm không có trong giỏ hàng"));

        cart.removeItem(itemToRemove); // Dùng helper method

        cartRepository.save(cart);
        return convertToCartDto(cart);
    }


    // =================================================================
    // HÀM HELPER
    // =================================================================

    /**
     * Lấy giỏ hàng của user, nếu chưa có thì tạo mới
     */
    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy user"));
            
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setCreatedAt(LocalDateTime.now());
            return cartRepository.save(newCart);
        });
    }

    /**
     * Chuyển Cart Entity -> CartDto
     */
    private CartDto convertToCartDto(Cart cart) {
        List<CartItemDto> itemDtos = cart.getItems().stream()
                .map(this::convertToCartItemDto)
                .collect(Collectors.toList());

        BigDecimal subTotal = itemDtos.stream()
                .map(CartItemDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = itemDtos.stream()
                .mapToInt(CartItemDto::getQuantity)
                .sum();

        return CartDto.builder()
                .cartId(cart.getId())
                .items(itemDtos)
                .totalItems(totalItems)
                .subTotal(subTotal)
                .build();
    }

    /**
     * Chuyển CartItem Entity -> CartItemDto
     */
    private CartItemDto convertToCartItemDto(CartItem item) {
        ProductVariant variant = item.getVariant();
        BigDecimal unitPrice = getEffectivePrice(variant);
        
        return CartItemDto.builder()
                .cartItemId(item.getId())
                .variantId(variant.getId())
                .productName(variant.getProduct().getName())
                .brandName(variant.getProduct().getBrand().getName())
                .size(variant.getSize())
                .color(variant.getColor())
                .imageUrl(variant.getImageUrl())
                .quantity(item.getQuantity())
                .unitPrice(unitPrice)
                .totalPrice(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }

    /**
     * Helper: Lấy giá cuối cùng (sale hoặc gốc)
     */
    private BigDecimal getEffectivePrice(ProductVariant variant) {
        return (variant.getPriceSale() != null && variant.getPriceSale().compareTo(BigDecimal.ZERO) > 0)
                ? variant.getPriceSale()
                : variant.getPriceBase();
    }
}