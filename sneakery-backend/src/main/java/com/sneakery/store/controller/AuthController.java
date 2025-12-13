package com.sneakery.store.controller;

import com.sneakery.store.dto.AuthResponseDto; // SỬA LỖI: Import DTO mới
import com.sneakery.store.dto.LoginDto;
import com.sneakery.store.dto.RegisterDto;
import com.sneakery.store.dto.ForgotPasswordRequestDto;
import com.sneakery.store.dto.ResetPasswordRequestDto;
import com.sneakery.store.dto.UpdateProfileDto;
import com.sneakery.store.dto.ChangePasswordDto;
import com.sneakery.store.dto.UserDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor; // SỬA LỖI: Dùng Constructor Injection
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller xử lý xác thực và đăng nhập/đăng ký
 * 
 * <p>Controller này cung cấp các API endpoints cho:
 * <ul>
 *   <li>Đăng ký tài khoản mới</li>
 *   <li>Đăng nhập</li>
 *   <li>Quên mật khẩu và đặt lại mật khẩu</li>
 *   <li>Lấy và cập nhật thông tin profile</li>
 *   <li>Đổi mật khẩu</li>
 * </ul>
 * 
 * <p><b>Về xác thực:</b>
 * <ul>
 *   <li>Đăng ký và đăng nhập: Không cần xác thực (public endpoints)</li>
 *   <li>Profile và đổi mật khẩu: Yêu cầu đăng nhập (isAuthenticated())</li>
 *   <li>Sau khi đăng nhập thành công, trả về JWT token trong AuthResponseDto</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Đăng ký tài khoản mới
 * RegisterDto registerDto = new RegisterDto();
 * registerDto.setEmail("user@example.com");
 * registerDto.setPassword("password123");
 * registerDto.setFullName("Nguyễn Văn A");
 * ResponseEntity&lt;AuthResponseDto&gt; response = authController.register(registerDto);
 * 
 * // Đăng nhập
 * LoginDto loginDto = new LoginDto();
 * loginDto.setEmail("user@example.com");
 * loginDto.setPassword("password123");
 * ResponseEntity&lt;AuthResponseDto&gt; loginResponse = authController.login(loginDto);
 * String token = loginResponse.getBody().getToken(); // JWT token
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Tag(name = "Authentication", description = "API xử lý xác thực, đăng nhập, đăng ký và quản lý profile")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AuthController {

    private final AuthService authService;

    /**
     * Đăng ký tài khoản mới
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Validate dữ liệu đầu vào (email, password, fullName)</li>
     *   <li>Kiểm tra email đã tồn tại chưa</li>
     *   <li>Tạo tài khoản mới trong database</li>
     *   <li>Mã hóa mật khẩu trước khi lưu</li>
     *   <li>Tạo JWT token cho user mới</li>
     *   <li>Trả về thông tin user và token</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Email phải unique (không được trùng với email đã tồn tại)</li>
     *   <li>Password sẽ được mã hóa bằng BCrypt trước khi lưu</li>
     *   <li>Sau khi đăng ký thành công, user có thể đăng nhập ngay với token được trả về</li>
     * </ul>
     * 
     * @param registerDto DTO chứa thông tin đăng ký:
     *                    - email: Email của user (bắt buộc, phải hợp lệ)
     *                    - password: Mật khẩu (bắt buộc, tối thiểu 6 ký tự)
     *                    - fullName: Họ tên đầy đủ (bắt buộc)
     * @return ResponseEntity chứa AuthResponseDto với thông tin user và JWT token
     * @throws ApiException nếu email đã tồn tại hoặc validation thất bại
     * 
     * @example
     * <pre>
     * RegisterDto registerDto = new RegisterDto();
     * registerDto.setEmail("newuser@example.com");
     * registerDto.setPassword("password123");
     * registerDto.setFullName("Nguyễn Văn A");
     * 
     * ResponseEntity&lt;AuthResponseDto&gt; response = authController.register(registerDto);
     * AuthResponseDto authResponse = response.getBody();
     * String token = authResponse.getToken(); // JWT token để dùng cho các request sau
     * </pre>
     */
    @Operation(
        summary = "Đăng ký tài khoản mới",
        description = "Tạo tài khoản mới cho user. Sau khi đăng ký thành công, trả về JWT token để sử dụng cho các request sau."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Đăng ký thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "409", description = "Email đã tồn tại")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterDto registerDto) {
        AuthResponseDto response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Đăng nhập
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Validate dữ liệu đầu vào (email, password)</li>
     *   <li>Tìm user theo email</li>
     *   <li>Kiểm tra mật khẩu có đúng không (so sánh với BCrypt hash)</li>
     *   <li>Tạo JWT token cho user</li>
     *   <li>Trả về thông tin user và token</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Email và password phải khớp với tài khoản đã đăng ký</li>
     *   <li>Token có thời hạn (thường là 24 giờ, cấu hình trong SecurityConfig)</li>
     *   <li>Token này cần được gửi kèm trong header "Authorization: Bearer {token}" cho các request sau</li>
     * </ul>
     * 
     * @param loginDto DTO chứa thông tin đăng nhập:
     *                 - email: Email của user (bắt buộc)
     *                 - password: Mật khẩu (bắt buộc)
     * @return ResponseEntity chứa AuthResponseDto với thông tin user và JWT token
     * @throws ApiException nếu email/password không đúng hoặc user không tồn tại
     * 
     * @example
     * <pre>
     * LoginDto loginDto = new LoginDto();
     * loginDto.setEmail("user@example.com");
     * loginDto.setPassword("password123");
     * 
     * ResponseEntity&lt;AuthResponseDto&gt; response = authController.login(loginDto);
     * AuthResponseDto authResponse = response.getBody();
     * String token = authResponse.getToken(); // JWT token
     * 
     * // Sử dụng token cho các request sau:
     * // Header: Authorization: Bearer {token}
     * </pre>
     */
    @Operation(
        summary = "Đăng nhập",
        description = "Đăng nhập với email và password. Trả về JWT token để sử dụng cho các request sau."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Đăng nhập thành công"),
        @ApiResponse(responseCode = "401", description = "Email hoặc password không đúng")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        AuthResponseDto response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Quên mật khẩu - Gửi email đặt lại mật khẩu
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Validate email</li>
     *   <li>Tìm user theo email</li>
     *   <li>Tạo token đặt lại mật khẩu (reset token)</li>
     *   <li>Lưu token vào database (có thời hạn, thường là 1 giờ)</li>
     *   <li>Gửi email chứa link đặt lại mật khẩu</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Nếu email không tồn tại, vẫn trả về thành công (để bảo mật, không tiết lộ email có tồn tại hay không)</li>
     *   <li>Reset token có thời hạn (thường là 1 giờ)</li>
     *   <li>Link đặt lại mật khẩu sẽ chứa token này</li>
     * </ul>
     * 
     * @param request DTO chứa email cần đặt lại mật khẩu
     * @return ResponseEntity với thông báo thành công
     * 
     * @example
     * <pre>
     * ForgotPasswordRequestDto request = new ForgotPasswordRequestDto();
     * request.setEmail("user@example.com");
     * 
     * ResponseEntity&lt;?&gt; response = authController.forgotPassword(request);
     * // Email chứa link đặt lại mật khẩu sẽ được gửi đến user
     * </pre>
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Nếu email tồn tại, hệ thống sẽ gửi link đặt lại mật khẩu.");
    }

    /**
     * Đặt lại mật khẩu - Sử dụng token từ email
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Validate token và mật khẩu mới</li>
     *   <li>Tìm user theo reset token</li>
     *   <li>Kiểm tra token còn hiệu lực không (chưa hết hạn)</li>
     *   <li>Mã hóa mật khẩu mới bằng BCrypt</li>
     *   <li>Cập nhật mật khẩu mới vào database</li>
     *   <li>Xóa reset token (để không thể dùng lại)</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Token phải hợp lệ và chưa hết hạn</li>
     *   <li>Mỗi token chỉ có thể dùng 1 lần</li>
     *   <li>Sau khi đặt lại mật khẩu thành công, user cần đăng nhập lại với mật khẩu mới</li>
     * </ul>
     * 
     * @param request DTO chứa:
     *                - token: Reset token từ email (bắt buộc)
     *                - newPassword: Mật khẩu mới (bắt buộc, tối thiểu 6 ký tự)
     * @return ResponseEntity với thông báo thành công
     * @throws ApiException nếu token không hợp lệ hoặc đã hết hạn
     * 
     * @example
     * <pre>
     * ResetPasswordRequestDto request = new ResetPasswordRequestDto();
     * request.setToken("reset-token-from-email");
     * request.setNewPassword("newPassword123");
     * 
     * ResponseEntity&lt;?&gt; response = authController.resetPassword(request);
     * // Mật khẩu đã được đặt lại thành công
     * </pre>
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Đặt lại mật khẩu thành công!");
    }

    /**
     * Lấy thông tin profile của user hiện tại
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy thông tin user từ JWT token (AuthenticationPrincipal)</li>
     *   <li>Lấy thông tin chi tiết user từ database</li>
     *   <li>Chuyển đổi sang DTO và trả về</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Yêu cầu đăng nhập (isAuthenticated())</li>
     *   <li>User được lấy từ JWT token, không cần truyền ID</li>
     *   <li>Trả về thông tin của chính user đang đăng nhập</li>
     * </ul>
     * 
     * @param currentUser User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa UserDto với thông tin profile
     * 
     * @example
     * <pre>
     * // Request với header: Authorization: Bearer {token}
     * ResponseEntity&lt;UserDto&gt; response = authController.getProfile(currentUser);
     * UserDto user = response.getBody();
     * System.out.println(user.getFullName()); // "Nguyễn Văn A"
     * System.out.println(user.getEmail()); // "user@example.com"
     * </pre>
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getProfile(@AuthenticationPrincipal User currentUser) {
        UserDto userDto = authService.getProfile(currentUser.getId());
        return ResponseEntity.ok(userDto);
    }

    /**
     * Cập nhật thông tin profile của user hiện tại
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy thông tin user từ JWT token</li>
     *   <li>Validate dữ liệu đầu vào (fullName, phone, address, v.v.)</li>
     *   <li>Cập nhật thông tin user trong database</li>
     *   <li>Trả về thông tin user sau khi cập nhật</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Yêu cầu đăng nhập (isAuthenticated())</li>
     *   <li>Chỉ có thể cập nhật profile của chính mình</li>
     *   <li>Email không thể thay đổi (cần dùng chức năng riêng nếu muốn đổi email)</li>
     * </ul>
     * 
     * @param updateProfileDto DTO chứa thông tin mới cần cập nhật:
     *                         - fullName: Họ tên (tùy chọn)
     *                         - phone: Số điện thoại (tùy chọn, phải hợp lệ nếu có)
     *                         - address: Địa chỉ (tùy chọn)
     * @param currentUser User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa UserDto với thông tin profile sau khi cập nhật
     * 
     * @example
     * <pre>
     * UpdateProfileDto updateData = new UpdateProfileDto();
     * updateData.setFullName("Nguyễn Văn B");
     * updateData.setPhone("0901234567");
     * 
     * ResponseEntity&lt;UserDto&gt; response = authController.updateProfile(updateData, currentUser);
     * UserDto updatedUser = response.getBody();
     * </pre>
     */
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> updateProfile(
            @Valid @RequestBody UpdateProfileDto updateProfileDto,
            @AuthenticationPrincipal User currentUser
    ) {
        UserDto updatedUser = authService.updateProfile(currentUser.getId(), updateProfileDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Đổi mật khẩu của user hiện tại
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy thông tin user từ JWT token</li>
     *   <li>Validate dữ liệu đầu vào (oldPassword, newPassword)</li>
     *   <li>Kiểm tra mật khẩu cũ có đúng không</li>
     *   <li>Mã hóa mật khẩu mới bằng BCrypt</li>
     *   <li>Cập nhật mật khẩu mới vào database</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Yêu cầu đăng nhập (isAuthenticated())</li>
     *   <li>Mật khẩu cũ phải đúng</li>
     *   <li>Mật khẩu mới phải khác mật khẩu cũ</li>
     *   <li>Sau khi đổi mật khẩu thành công, user vẫn đăng nhập (không cần đăng nhập lại)</li>
     * </ul>
     * 
     * @param changePasswordDto DTO chứa:
     *                          - oldPassword: Mật khẩu cũ (bắt buộc)
     *                          - newPassword: Mật khẩu mới (bắt buộc, tối thiểu 6 ký tự)
     * @param currentUser User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity với thông báo thành công
     * @throws ApiException nếu mật khẩu cũ không đúng hoặc validation thất bại
     * 
     * @example
     * <pre>
     * ChangePasswordDto changePasswordDto = new ChangePasswordDto();
     * changePasswordDto.setOldPassword("oldPassword123");
     * changePasswordDto.setNewPassword("newPassword123");
     * 
     * ResponseEntity&lt;?&gt; response = authController.changePassword(changePasswordDto, currentUser);
     * // Mật khẩu đã được đổi thành công
     * </pre>
     */
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordDto changePasswordDto,
            @AuthenticationPrincipal User currentUser
    ) {
        authService.changePassword(currentUser.getId(), changePasswordDto);
        return ResponseEntity.ok("Đổi mật khẩu thành công!");
    }
}