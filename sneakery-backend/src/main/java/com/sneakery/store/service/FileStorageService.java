package com.sneakery.store.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * Service: FileStorageService
 * ---------------------------
 * Lưu trữ file vật lý cho ảnh sản phẩm.
 * Thư mục uploads nằm cùng cấp với src/ (project root).
 */
@Slf4j
@Service
public class FileStorageService {

    /**
     * Thư mục gốc lưu file — mặc định: uploads/
     * Có thể override bằng app.file.upload-dir trong application.properties
     */
    @Value("${app.file.upload-dir:uploads}")
    private String rootUploadDir;

    /**
     * Lưu file ảnh sản phẩm thật vào thư mục uploads/sanpham/{productId}/
     * @param productId id sản phẩm
     * @param file file ảnh
     * @return đường dẫn URL tương đối (ví dụ: /uploads/sanpham/6/abc.jpg)
     */
    public String storeProductImage(Long productId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File upload rỗng hoặc null!");
        }

        try {
            // Gốc: uploads/
            Path rootPath = Paths.get(rootUploadDir).toAbsolutePath().normalize();

            // Thư mục: uploads/sanpham/{id}/
            Path targetDir = rootPath.resolve("sanpham/" + productId).normalize();
            Files.createDirectories(targetDir);

            // Tạo tên file duy nhất
            String originalName = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = "";

            int dotIndex = originalName.lastIndexOf('.');
            if (dotIndex >= 0) ext = originalName.substring(dotIndex);

            String newName = UUID.randomUUID() + ext;
            Path targetPath = targetDir.resolve(newName);

            // Ghi file vật lý
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Trả về URL dùng trong FE
            String relativeUrl = "/uploads/sanpham/" + productId + "/" + newName;
            log.info("📁 Đã lưu file: {}", relativeUrl);

            return relativeUrl;
        } catch (IOException e) {
            log.error("❌ Lỗi khi lưu file sản phẩm {}: {}", productId, e.getMessage());
            throw new RuntimeException("Không thể lưu file: " + e.getMessage());
        }
    }

    /**
     * Xóa file vật lý theo URL (VD: /uploads/sanpham/6/abc.jpg)
     */
    public void deleteFileByUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        try {
            String cleaned = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;
            Path filePath = Paths.get(cleaned).toAbsolutePath().normalize();

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("🗑️ Đã xóa file: {}", filePath);
            } else {
                log.warn("⚠️ File không tồn tại: {}", filePath);
            }
        } catch (IOException e) {
            log.error("❌ Không thể xóa file: {}", e.getMessage());
        }
    }
}
