// file: com/sneakery/store/service/FileStorageService.java
package com.sneakery.store.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sneakery.store.constants.ProductConstants;
import com.sneakery.store.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * Service xử lý upload và quản lý file với Cloudinary.
 * 
 * <p>Bao gồm validation đầy đủ cho file upload:
 * <ul>
 *   <li>File type validation (chỉ cho phép image)</li>
 *   <li>File size validation (giới hạn kích thước)</li>
 *   <li>File extension validation</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final Cloudinary cloudinary;

    /**
     * Kết quả upload Cloudinary (URL + PublicId)
     */
    public record CloudinaryUploadResult(String url, String publicId) {}

    /**
     * Upload ảnh sản phẩm vào Cloudinary với validation đầy đủ
     * 
     * <p>Validation bao gồm:
     * <ul>
     *   <li>Kiểm tra file không null/empty</li>
     *   <li>Kiểm tra file type (chỉ cho phép image/jpeg, image/png, image/webp)</li>
     *   <li>Kiểm tra file size (tối đa 5MB)</li>
     *   <li>Kiểm tra file extension</li>
     * </ul>
     * 
     * @param productId ID sản phẩm
     * @param file MultipartFile cần upload
     * @return CloudinaryUploadResult chứa URL và publicId
     * @throws ApiException nếu validation fails
     */
    public CloudinaryUploadResult storeProductImage(Long productId, MultipartFile file) {
        // 1. Validate file không null/empty
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "File upload rỗng hoặc null!");
        }

        // 2. Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(ProductConstants.ALLOWED_IMAGE_TYPES).contains(contentType.toLowerCase())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 
                "File type không được hỗ trợ. Chỉ chấp nhận: JPG, PNG, WEBP");
        }

        // 3. Validate file size
        if (file.getSize() > ProductConstants.MAX_IMAGE_FILE_SIZE) {
            long maxSizeMB = ProductConstants.MAX_IMAGE_FILE_SIZE / (1024 * 1024);
            throw new ApiException(HttpStatus.BAD_REQUEST, 
                String.format("Kích thước file vượt quá %dMB. Kích thước hiện tại: %.2fMB", 
                    maxSizeMB, file.getSize() / (1024.0 * 1024.0)));
        }

        // 4. Validate file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.toLowerCase();
            boolean hasValidExtension = Arrays.stream(ProductConstants.ALLOWED_IMAGE_EXTENSIONS)
                    .anyMatch(extension::endsWith);
            if (!hasValidExtension) {
                throw new ApiException(HttpStatus.BAD_REQUEST, 
                    "File extension không hợp lệ. Chỉ chấp nhận: .jpg, .jpeg, .png, .webp");
            }
        }

        // 5. Upload lên Cloudinary
        try {
            Map<?, ?> res = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "uploads/sanpham/" + productId,
                            "use_filename", true,
                            "unique_filename", true,
                            "resource_type", "image"
                    )
            );
            String url = res.get("secure_url").toString();
            String publicId = res.get("public_id").toString();
            log.info("✅ Uploaded Cloudinary: url={}, publicId={}", url, publicId);
            return new CloudinaryUploadResult(url, publicId);
        } catch (IOException e) {
            log.error("❌ Upload Cloudinary lỗi: {}", e.getMessage(), e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Không thể upload file: " + e.getMessage());
        }
    }

    /**
     * Xoá asset Cloudinary bằng public_id (chính xác 100%).
     */
    public void deleteByPublicId(String publicId) {
        if (publicId == null || publicId.isBlank()) return;
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("🗑️ Đã xoá Cloudinary asset: {}", publicId);
        } catch (Exception e) {
            log.warn("⚠️ Không thể xoá Cloudinary asset {}: {}", publicId, e.getMessage());
        }
    }

    /**
     * (Giữ để tương thích ảnh cũ) — tìm public_id từ URL nếu cần.
     * ƯU TIÊN dùng deleteByPublicId().
     */
    public void deleteByUrlBestEffort(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;
        try {
            String publicId = extractPublicIdFromUrl(imageUrl);
            deleteByPublicId(publicId);
        } catch (Exception e) {
            log.warn("⚠️ Không thể suy ra public_id từ URL {}: {}", imageUrl, e.getMessage());
        }
    }

    /**
     * Parse public_id từ URL: https://res.cloudinary.com/.../image/upload/v123456/uploads/sanpham/23/abc123.jpg
     * => public_id = uploads/sanpham/23/abc123
     */
    private String extractPublicIdFromUrl(String url) {
        int uploadIndex = url.indexOf("/upload/");
        if (uploadIndex == -1) return url;

        String afterUpload = url.substring(uploadIndex + "/upload/".length());
        // Bỏ version vXXXXX/ nếu có
        afterUpload = afterUpload.replaceFirst("^v\\d+/", "");
        // Bỏ extension
        afterUpload = afterUpload.replaceFirst("\\.[^.]+$", "");
        return afterUpload;
    }
}
