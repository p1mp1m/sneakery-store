package com.sneakery.store.controller;

import com.sneakery.store.dto.AddressDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * Controller xử lý địa chỉ giao hàng cho User
 * 
 * <p>Controller này cung cấp các API endpoints cho user để quản lý địa chỉ giao hàng:
 * <ul>
 *   <li>Lấy danh sách địa chỉ của user</li>
 *   <li>Lấy thông tin chi tiết địa chỉ theo ID</li>
 *   <li>Tạo địa chỉ mới</li>
 *   <li>Cập nhật địa chỉ</li>
 *   <li>Xóa địa chỉ</li>
 * </ul>
 * 
 * <p><b>Về bảo mật:</b>
 * <ul>
 *   <li>Tất cả endpoints đều yêu cầu đăng nhập (isAuthenticated())</li>
 *   <li>User chỉ có thể quản lý địa chỉ của chính mình</li>
 *   <li>User được lấy từ JWT token (AuthenticationPrincipal)</li>
 * </ul>
 * 
 * <p><b>Về địa chỉ:</b>
 * <ul>
 *   <li>Mỗi user có thể có nhiều địa chỉ giao hàng</li>
 *   <li>Địa chỉ bao gồm: tên người nhận, số điện thoại, địa chỉ chi tiết, tỉnh/thành phố, quận/huyện</li>
 *   <li>Địa chỉ được sử dụng khi checkout đơn hàng</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Lấy danh sách địa chỉ
 * ResponseEntity&lt;List&lt;AddressDto&gt;&gt; response = addressController.getMyAddresses(currentUser);
 * 
 * // Tạo địa chỉ mới
 * AddressDto newAddress = new AddressDto();
 * newAddress.setFullName("Nguyễn Văn A");
 * newAddress.setPhone("0901234567");
 * newAddress.setAddress("123 Đường ABC");
 * ResponseEntity&lt;AddressDto&gt; response2 = addressController.createMyAddress(newAddress, currentUser);
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Tag(name = "Addresses", description = "API quản lý địa chỉ giao hàng cho User")
@Slf4j
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AddressController {

    private final AddressService addressService;

    /**
     * Lấy danh sách tất cả địa chỉ của user hiện tại
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Gọi service để lấy tất cả địa chỉ của user</li>
     *   <li>Trả về danh sách địa chỉ</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Mỗi địa chỉ bao gồm: ID, tên người nhận, số điện thoại, địa chỉ chi tiết, tỉnh/thành phố, quận/huyện</li>
     *   <li>Danh sách được sắp xếp theo ngày tạo (mới nhất trước)</li>
     * </ul>
     * 
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa danh sách AddressDto (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;List&lt;AddressDto&gt;&gt; response = addressController.getMyAddresses(currentUser);
     * List&lt;AddressDto&gt; addresses = response.getBody();
     * addresses.forEach(addr -&gt; System.out.println(addr.getFullName()));
     * </pre>
     */
    @GetMapping
    public ResponseEntity<List<AddressDto>> getMyAddresses(
            @AuthenticationPrincipal User userPrincipal
    ) {
        log.info("📍 GET /api/addresses - User: {}", userPrincipal.getId());
        List<AddressDto> addresses = addressService.getAddressesByUserId(userPrincipal.getId());
        return ResponseEntity.ok(addresses);
    }

    /**
     * Lấy thông tin chi tiết địa chỉ theo ID
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Gọi service để lấy địa chỉ theo ID</li>
     *   <li>Kiểm tra địa chỉ có thuộc về user hiện tại không</li>
     *   <li>Trả về thông tin chi tiết địa chỉ</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b> User chỉ có thể xem địa chỉ của chính mình.
     * Nếu cố gắng xem địa chỉ của user khác, sẽ throw ApiException.
     * 
     * @param id ID của địa chỉ cần lấy
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa AddressDto với thông tin chi tiết địa chỉ (HTTP 200 OK)
     * @throws ApiException nếu không tìm thấy địa chỉ hoặc địa chỉ không thuộc về user hiện tại
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;AddressDto&gt; response = addressController.getMyAddressById(1L, currentUser);
     * AddressDto address = response.getBody();
     * System.out.println(address.getFullName()); // "Nguyễn Văn A"
     * </pre>
     */
    @GetMapping("/{id}")
    public ResponseEntity<AddressDto> getMyAddressById(
            @PathVariable Long id,
            @AuthenticationPrincipal User userPrincipal
    ) {
        log.info("📍 GET /api/addresses/{} - User: {}", id, userPrincipal.getId());
        AddressDto address = addressService.getAddressById(id, userPrincipal.getId());
        return ResponseEntity.ok(address);
    }

    /**
     * Tạo địa chỉ mới
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Validate dữ liệu đầu vào (tên, số điện thoại, địa chỉ)</li>
     *   <li>Gọi service để tạo địa chỉ mới</li>
     *   <li>Trả về địa chỉ vừa tạo</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Số điện thoại phải hợp lệ (theo format Việt Nam)</li>
     *   <li>Tất cả các trường bắt buộc phải được điền đầy đủ</li>
     *   <li>Địa chỉ sẽ tự động được gán cho user hiện tại</li>
     * </ul>
     * 
     * @param addressDto DTO chứa thông tin địa chỉ cần tạo:
     *                    - fullName: Tên người nhận (bắt buộc)
     *                    - phone: Số điện thoại (bắt buộc, phải hợp lệ)
     *                    - address: Địa chỉ chi tiết (bắt buộc)
     *                    - province: Tỉnh/thành phố (bắt buộc)
     *                    - district: Quận/huyện (bắt buộc)
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa AddressDto của địa chỉ vừa tạo (HTTP 201 Created)
     * @throws ApiException nếu validation thất bại
     * 
     * @example
     * <pre>
     * AddressDto newAddress = new AddressDto();
     * newAddress.setFullName("Nguyễn Văn A");
     * newAddress.setPhone("0901234567");
     * newAddress.setAddress("123 Đường ABC");
     * newAddress.setProvince("Hồ Chí Minh");
     * newAddress.setDistrict("Quận 1");
     * 
     * ResponseEntity&lt;AddressDto&gt; response = addressController.createMyAddress(newAddress, currentUser);
     * AddressDto created = response.getBody();
     * </pre>
     */
    @PostMapping
    public ResponseEntity<AddressDto> createMyAddress(
            @Valid @RequestBody AddressDto addressDto,
            @AuthenticationPrincipal User userPrincipal
    ) {
        log.info("📍 POST /api/addresses - User: {}", userPrincipal.getId());
        AddressDto newAddress = addressService.createAddress(addressDto, userPrincipal.getId());
        return new ResponseEntity<>(newAddress, HttpStatus.CREATED);
    }

    /**
     * Cập nhật thông tin địa chỉ
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Validate dữ liệu đầu vào</li>
     *   <li>Gọi service để cập nhật địa chỉ</li>
     *   <li>Kiểm tra địa chỉ có thuộc về user hiện tại không</li>
     *   <li>Trả về địa chỉ sau khi cập nhật</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>User chỉ có thể cập nhật địa chỉ của chính mình</li>
     *   <li>Tất cả các trường trong DTO sẽ được cập nhật (nếu có giá trị)</li>
     *   <li>Số điện thoại phải hợp lệ nếu được cập nhật</li>
     * </ul>
     * 
     * @param id ID của địa chỉ cần cập nhật
     * @param addressDto DTO chứa thông tin mới của địa chỉ
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa AddressDto của địa chỉ sau khi cập nhật (HTTP 200 OK)
     * @throws ApiException nếu không tìm thấy địa chỉ, địa chỉ không thuộc về user, hoặc validation thất bại
     * 
     * @example
     * <pre>
     * AddressDto updateData = new AddressDto();
     * updateData.setFullName("Nguyễn Văn B");
     * updateData.setPhone("0907654321");
     * 
     * ResponseEntity&lt;AddressDto&gt; response = addressController.updateMyAddress(1L, updateData, currentUser);
     * AddressDto updated = response.getBody();
     * </pre>
     */
    @PutMapping("/{id}")
    public ResponseEntity<AddressDto> updateMyAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressDto addressDto,
            @AuthenticationPrincipal User userPrincipal
    ) {
        log.info("📍 PUT /api/addresses/{} - User: {}", id, userPrincipal.getId());
        AddressDto updatedAddress = addressService.updateAddress(id, addressDto, userPrincipal.getId());
        return ResponseEntity.ok(updatedAddress);
    }

    /**
     * Xóa địa chỉ
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Gọi service để xóa địa chỉ</li>
     *   <li>Kiểm tra địa chỉ có thuộc về user hiện tại không</li>
     *   <li>Trả về HTTP 204 No Content</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>User chỉ có thể xóa địa chỉ của chính mình</li>
     *   <li>Hành động này không thể hoàn tác</li>
     *   <li>Nếu địa chỉ đang được sử dụng trong đơn hàng, vẫn có thể xóa (địa chỉ được lưu trong order)</li>
     * </ul>
     * 
     * @param id ID của địa chỉ cần xóa
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity với HTTP 204 No Content
     * @throws ApiException nếu không tìm thấy địa chỉ hoặc địa chỉ không thuộc về user hiện tại
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;Void&gt; response = addressController.deleteMyAddress(1L, currentUser);
     * // HTTP 204 No Content
     * </pre>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal User userPrincipal
    ) {
        log.info("📍 DELETE /api/addresses/{} - User: {}", id, userPrincipal.getId());
        addressService.deleteAddress(id, userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }
}