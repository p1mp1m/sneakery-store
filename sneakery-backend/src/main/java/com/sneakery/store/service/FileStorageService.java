// file: com/sneakery/store/service/FileStorageService.java
package com.sneakery.store.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Upload/Xoá ảnh với Cloudinary.
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
     * Upload ảnh vào folder uploads/sanpham/{productId} và trả về URL + publicId
     */
    public CloudinaryUploadResult storeProductImage(Long productId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File upload rỗng hoặc null!");
        }

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
            log.error("❌ Upload Cloudinary lỗi: {}", e.getMessage());
            throw new RuntimeException("Không thể upload file: " + e.getMessage());
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
