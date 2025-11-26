package com.sneakery.store.service;

import com.sneakery.store.dto.AddToCartRequestDto;
import com.sneakery.store.dto.CartDto;
import com.sneakery.store.dto.CartItemDto;
import com.sneakery.store.dto.UpdateCartItemRequestDto;
import com.sneakery.store.entity.Cart;
import com.sneakery.store.entity.CartItem;
import com.sneakery.store.entity.ProductVariant;
import com.sneakery.store.entity.ProductImage;
import com.sneakery.store.entity.User;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.CartRepository;
import com.sneakery.store.repository.ProductVariantRepository;
import com.sneakery.store.repository.ProductImageRepository;
import com.sneakery.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service xử lý giỏ hàng cho User
 * 
 * <p>Service này cung cấp các chức năng quản lý giỏ hàng cho user:
 * <ul>
 *   <li>Lấy giỏ hàng của user</li>
 *   <li>Thêm/Cập nhật sản phẩm vào giỏ hàng</li>
 *   <li>Xóa sản phẩm khỏi giỏ hàng</li>
 *   <li>Xóa toàn bộ giỏ hàng</li>
 * </ul>
 * 
 * <p><b>Về giỏ hàng:</b>
 * <ul>
 *   <li>Mỗi user có 1 giỏ hàng duy nhất</li>
 *   <li>Giỏ hàng được tự động tạo khi user thêm sản phẩm đầu tiên</li>
 *   <li>Giỏ hàng sẽ bị xóa sau khi checkout thành công</li>
 *   <li>Mỗi item trong giỏ hàng tương ứng với 1 variant (size, màu sắc)</li>
 * </ul>
 * 
 * <p><b>Về tồn kho:</b>
 * <ul>
 *   <li>Khi thêm sản phẩm vào giỏ hàng, hệ thống sẽ kiểm tra tồn kho</li>
 *   <li>Số lượng trong giỏ hàng không được vượt quá tồn kho</li>
 *   <li>Nếu tồn kho không đủ, sẽ throw ApiException</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Lấy giỏ hàng
 * CartDto cart = cartService.getCartByUserId(userId);
 * 
 * // Thêm sản phẩm vào giỏ hàng
 * AddToCartRequestDto request = new AddToCartRequestDto();
 * request.setVariantId(1L);
 * request.setQuantity(2);
 * CartDto updatedCart = cartService.addItemToCart(userId, request);
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository productImageRepository;

    /**
     * Lấy giỏ hàng của user
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy hoặc tạo giỏ hàng của user</li>
     *   <li>Sử dụng query tối ưu để load tất cả items trong 1 lần</li>
     *   <li>Chuyển đổi sang DTO và trả về</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Bao gồm tất cả items trong giỏ hàng: sản phẩm, variant, số lượng, giá</li>
     *   <li>Bao gồm tổng tiền của giỏ hàng</li>
     *   <li>Sử dụng query tối ưu (findByUserIdWithDetails) để load tất cả dữ liệu trong 1 lần</li>
     * </ul>
     * 
     * @param userId ID của user cần lấy giỏ hàng
     * @return CartDto chứa thông tin giỏ hàng (nếu chưa có giỏ hàng, sẽ tạo mới rỗng)
     * 
     * @example
     * <pre>
     * CartDto cart = cartService.getCartByUserId(userId);
     * System.out.println(cart.getItems().size()); // Số lượng items trong giỏ hàng
     * System.out.println(cart.getTotalAmount()); // Tổng tiền giỏ hàng
     * </pre>
     */
    @Transactional(readOnly = true)
    public CartDto getCartByUserId(Long userId) {
        Cart cart = getOrCreateCart(userId);
        // Dùng query đã tối ưu
        cart = cartRepository.findByUserIdWithDetails(userId).orElse(cart); 
        return convertToCartDto(cart);
    }

    /**
     * Thêm/Cập nhật sản phẩm vào giỏ hàng
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy hoặc tạo giỏ hàng của user</li>
     *   <li>Kiểm tra variant có tồn tại và còn tồn kho không</li>
     *   <li>Nếu variant đã có trong giỏ hàng: Cập nhật số lượng</li>
     *   <li>Nếu variant chưa có trong giỏ hàng: Thêm mới</li>
     *   <li>Kiểm tra tồn kho đủ cho số lượng mới</li>
     *   <li>Tính lại tổng tiền giỏ hàng</li>
     *   <li>Lưu vào database</li>
     *   <li>Trả về giỏ hàng sau khi cập nhật</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Nếu variant đã có trong giỏ hàng, số lượng sẽ được cập nhật (không cộng dồn)</li>
     *   <li>Số lượng phải > 0 và <= tồn kho của variant</li>
     *   <li>Nếu tồn kho không đủ, sẽ throw ApiException</li>
     *   <li>Sau khi cập nhật, tổng tiền giỏ hàng sẽ được tính lại tự động</li>
     * </ul>
     * 
     * @param userId ID của user đang thêm sản phẩm
     * @param requestDto DTO chứa thông tin sản phẩm cần thêm:
     *                   - variantId: ID của variant (bắt buộc)
     *                   - quantity: Số lượng (bắt buộc, phải > 0)
     * @return CartDto chứa giỏ hàng sau khi cập nhật
     * @throws ApiException nếu variant không tồn tại, hết tồn kho, hoặc validation thất bại
     * 
     * @example
     * <pre>
     * AddToCartRequestDto request = new AddToCartRequestDto();
     * request.setVariantId(1L); // Variant: Nike Air Max 90 - Size 40 - Đỏ
     * request.setQuantity(2); // Thêm 2 đôi
     * 
     * CartDto cart = cartService.addItemToCart(userId, request);
     * System.out.println(cart.getItems().size()); // Số lượng items trong giỏ hàng
     * </pre>
     */
    @Transactional
    public CartDto addItemToCart(Long userId, AddToCartRequestDto requestDto) {
        Cart cart = getOrCreateCart(userId);

        ProductVariant variant = variantRepository.findById(
                        Objects.requireNonNull(requestDto.getVariantId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm (variant)"));

        int addQuantity = requestDto.getQuantity();
        if (addQuantity <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Số lượng phải lớn hơn 0");
        }

        // Tìm item đã có trong giỏ
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getVariant().getId().equals(requestDto.getVariantId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();

            int newQty = item.getQuantity() + addQuantity; // 🔥 CỘNG DỒN

            if (newQty > variant.getStockQuantity()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Không đủ hàng tồn kho");
            }

            item.setQuantity(newQty);
        } else {
            if (addQuantity > variant.getStockQuantity()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Không đủ hàng tồn kho");
            }

            CartItem newItem = new CartItem();
            newItem.setVariant(variant);
            newItem.setQuantity(addQuantity);
            cart.addItem(newItem);
        }

        cartRepository.save(cart);

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

    /**
     * API 3.5: Xóa toàn bộ giỏ hàng (clear all items)
     */
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart != null && !cart.getItems().isEmpty()) {
            // Xóa tất cả items
            cart.getItems().clear();
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);
        }
    }

    // =================================================================
    // GUEST CART APIs
    // =================================================================

    /**
     * API 4: Lấy giỏ hàng của guest (theo session ID)
     */
    @Transactional(readOnly = true)
    public CartDto getCartBySessionId(String sessionId) {
        Cart cart = getOrCreateGuestCart(sessionId);
        // Dùng query đã tối ưu
        cart = cartRepository.findBySessionIdWithDetails(sessionId).orElse(cart);
        return convertToCartDto(cart);
    }

    /**
     * API 5: Thêm/Cập nhật sản phẩm vào guest cart
     */
    @Transactional
    public CartDto addItemToGuestCart(String sessionId, AddToCartRequestDto requestDto) {
        Cart cart = getOrCreateGuestCart(sessionId);

        ProductVariant variant = variantRepository.findById(
                        Objects.requireNonNull(requestDto.getVariantId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm (variant)"));

        int addQuantity = requestDto.getQuantity();
        if (addQuantity <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Số lượng phải lớn hơn 0");
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getVariant().getId().equals(requestDto.getVariantId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();

            int newQty = item.getQuantity() + addQuantity; // 🔥 CỘNG DỒN

            if (newQty > variant.getStockQuantity()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Không đủ hàng tồn kho");
            }

            item.setQuantity(newQty);
        } else {
            if (addQuantity > variant.getStockQuantity()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Không đủ hàng tồn kho");
            }

            CartItem newItem = new CartItem();
            newItem.setVariant(variant);
            newItem.setQuantity(addQuantity);
            cart.addItem(newItem);
        }

        cartRepository.save(cart);

        return getCartBySessionId(sessionId);
    }


    /**
     * API 6: Xóa sản phẩm khỏi guest cart
     */
    @Transactional
    public CartDto removeItemFromGuestCart(String sessionId, Long variantId) {
        Cart cart = getOrCreateGuestCart(sessionId);

        // Tìm item trong giỏ
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getVariant().getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Sản phẩm không có trong giỏ hàng"));

        cart.removeItem(itemToRemove);

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
            User user = userRepository.findById(Objects.requireNonNull(userId))
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy user"));
            
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setCreatedAt(LocalDateTime.now());
            return cartRepository.save(newCart);
        });
    }

    /**
     * Lấy giỏ hàng của guest (theo session ID), nếu chưa có thì tạo mới
     */
    private Cart getOrCreateGuestCart(String sessionId) {
        return cartRepository.findBySessionId(sessionId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setSessionId(sessionId);
            newCart.setUser(null); // Guest cart không có user
            newCart.setCreatedAt(LocalDateTime.now());
            // Set expiration time: 7 days from now
            newCart.setExpiresAt(LocalDateTime.now().plusDays(7));
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
        
        // Lấy imageUrl từ variant, nếu null hoặc rỗng thì lấy ảnh primary từ Product_Images
//        String imageUrl = variant.getImageUrl();
//        if ((imageUrl == null || imageUrl.isBlank()) && variant.getProduct() != null) {
//            Long productId = variant.getProduct().getId();
//            Optional<ProductImage> coverImage = productImageRepository.findByProductIdAndIsPrimaryTrue(productId);
//            if (coverImage.isPresent()) {
//                imageUrl = coverImage.get().getImageUrl();
//            }
//        }
        
        return CartItemDto.builder()
                .cartItemId(item.getId())
                .variantId(variant.getId())
                .productName(variant.getProduct().getName())
                .brandName(variant.getProduct().getBrand().getName())
                .size(variant.getSize())
                .color(variant.getColor())
//                .imageUrl(imageUrl)
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

    @Transactional
    public CartDto updateItemQuantity(Long userId, UpdateCartItemRequestDto requestDto) {
        Cart cart = getOrCreateCart(userId);

        ProductVariant variant = variantRepository.findById(
                        Objects.requireNonNull(requestDto.getVariantId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể sản phẩm"));

        int newQuantity = requestDto.getQuantity();

        if (newQuantity <= 0)
            throw new ApiException(HttpStatus.BAD_REQUEST, "Số lượng phải lớn hơn 0");

        if (newQuantity > variant.getStockQuantity())
            throw new ApiException(HttpStatus.BAD_REQUEST, "Không đủ hàng tồn kho");

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getVariant().getId().equals(requestDto.getVariantId()))
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Sản phẩm không có trong giỏ"));

        // ✅ SET – KHÔNG CỘNG
        item.setQuantity(newQuantity);

        cartRepository.save(cart);

        // Load lại bằng query tối ưu
        return getCartByUserId(userId);
    }

}