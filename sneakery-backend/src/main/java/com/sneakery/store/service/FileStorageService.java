// file: com/sneakery/store/service/FileStorageService.java
package com.sneakery.store.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.sneakery.store.constants.ProductConstants;
import com.sneakery.store.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

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
public class FileStorageService {

    private final Cloudinary cloudinary;
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    // Constructor với @Autowired(required = false) để cho phép cloudinary null
    public FileStorageService(@Autowired(required = false) Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

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

        // 5. Upload lên Cloudinary hoặc lưu local
        if (cloudinary != null) {
            try {
                log.info("📤 Uploading to Cloudinary: productId={}, filename={}, size={} bytes", 
                    productId, file.getOriginalFilename(), file.getSize());
                
                // Upload file gốc lên Cloudinary
                // Transformations sẽ được apply khi generate URL (trong CloudinaryUtil)
                // Upload file gốc để có thể tạo nhiều size khác nhau sau này
                Map<?, ?> res = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "folder", "uploads/sanpham/" + productId,
                                "use_filename", true,
                                "unique_filename", true,
                                "resource_type", "image"
                                // Không apply transformations khi upload
                                // Transformations sẽ được thêm vào URL khi cần (thumbnail, medium, large)
                        )
                );
                
                String url = res.get("secure_url").toString();
                String publicId = res.get("public_id").toString();
                log.info("✅ Uploaded Cloudinary successfully: url={}, publicId={}", url, publicId);
                return new CloudinaryUploadResult(url, publicId);
            } catch (Exception e) {
                log.error("❌ Upload Cloudinary lỗi: {}", e.getMessage(), e);
                log.error("❌ Error details - Class: {}, Cause: {}", 
                    e.getClass().getName(), 
                    e.getCause() != null ? e.getCause().getMessage() : "N/A");
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Không thể upload file lên Cloudinary: " + e.getMessage());
            }
        } else {
            // Fallback: Lưu file local
            log.warn("⚠️ Cloudinary chưa được cấu hình, sử dụng local storage");
            try {
                return storeProductImageLocal(productId, file);
            } catch (IOException e) {
                log.error("❌ Upload local file lỗi: {}", e.getMessage(), e);
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Không thể upload file: " + e.getMessage());
            }
        }
    }

    /**
     * Lưu file local (fallback khi không có Cloudinary)
     */
    private CloudinaryUploadResult storeProductImageLocal(Long productId, MultipartFile file) throws IOException {
        // Tạo thư mục nếu chưa có
        Path productDir = Paths.get(uploadDir, "sanpham", productId.toString());
        Files.createDirectories(productDir);
        
        // Tạo tên file unique
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = productDir.resolve(filename);
        
        // Lưu file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Tạo URL (relative path)
        String url = "/" + uploadDir + "/sanpham/" + productId + "/" + filename;
        String publicId = uploadDir + "/sanpham/" + productId + "/" + filename;
        
        log.info("✅ Uploaded local file: url={}, path={}", url, filePath);
        return new CloudinaryUploadResult(url, publicId);
    }
    
    /**
     * Xoá asset Cloudinary bằng public_id (chính xác 100%).
     */
    public void deleteByPublicId(String publicId) {
        if (publicId == null || publicId.isBlank()) return;
        
        if (cloudinary != null) {
            try {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("🗑️ Đã xoá Cloudinary asset: {}", publicId);
            } catch (Exception e) {
                log.warn("⚠️ Không thể xoá Cloudinary asset {}: {}", publicId, e.getMessage());
            }
        } else {
            // Fallback: Xóa file local
            try {
                // publicId có thể là relative path như "uploads/sanpham/2/abc.jpg"
                Path filePath = Paths.get(publicId);
                if (!Files.exists(filePath)) {
                    // Thử với absolute path từ project root
                    filePath = Paths.get(System.getProperty("user.dir"), publicId);
                }
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    log.info("🗑️ Đã xoá local file: {}", filePath);
                } else {
                    log.warn("⚠️ Không tìm thấy file để xóa: {}", publicId);
                }
            } catch (Exception e) {
                log.warn("⚠️ Không thể xoá local file {}: {}", publicId, e.getMessage());
            }
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

    /**
     * Tạo signed URL (dùng cho private assets)
     * 
     * <p>Lưu ý: Để sử dụng signed URLs, cần cấu hình upload preset với signing mode = "signed"
     * trong Cloudinary Dashboard. Signed URLs được tạo tự động khi upload với signed preset.
     * 
     * <p>Để tạo signed URL manually, cần tính toán signature từ API secret.
     * Xem thêm: https://cloudinary.com/documentation/advanced_url_delivery_options#generating_delivery_url_signatures
     * 
     * @param publicId Public ID của ảnh trên Cloudinary
     * @return Signed URL (nếu có) hoặc null
     */
    public String generateSignedUrl(String publicId) {
        if (cloudinary == null || publicId == null || publicId.isBlank()) {
            return null;
        }
        
        // Note: Signed URLs thường được tạo khi upload với signed preset
        // Hoặc có thể tính toán signature manually nếu cần
        // Ở đây ta chỉ return null, implement sau nếu cần
        log.debug("🔐 Signed URL generation - implement if needed for private assets");
        return null;
    }

    /**
     * Tạo URL với transformations (resize, crop, format, quality)
     * 
     * @param publicId Public ID của ảnh
     * @param width Chiều rộng (null = không resize)
     * @param height Chiều cao (null = không resize)
     * @param crop Loại crop: "limit", "fill", "fit", "scale", "thumb" (null = không crop)
     * @param quality Chất lượng: "auto", "80", "90", "best" (null = auto)
     * @param format Định dạng: "auto", "webp", "jpg", "png" (null = auto)
     * @return URL với transformations
     */
    public String generateTransformedUrl(String publicId, Integer width, Integer height, 
                                         String crop, String quality, String format) {
        if (cloudinary == null || publicId == null || publicId.isBlank()) {
            return null;
        }
        
        try {
            com.cloudinary.Url url = cloudinary.url().publicId(publicId);
            
            // Build transformation string
            StringBuilder transformation = new StringBuilder();
            if (width != null) transformation.append("w_").append(width).append(",");
            if (height != null) transformation.append("h_").append(height).append(",");
            if (crop != null && !crop.isBlank()) transformation.append("c_").append(crop).append(",");
            if (quality != null && !quality.isBlank()) transformation.append("q_").append(quality).append(",");
            if (format != null && !format.isBlank()) transformation.append("f_").append(format).append(",");
            
            // Remove trailing comma
            if (transformation.length() > 0 && transformation.charAt(transformation.length() - 1) == ',') {
                transformation.setLength(transformation.length() - 1);
            }
            
            if (transformation.length() > 0) {
                @SuppressWarnings("rawtypes")
                Transformation trans = new Transformation().rawTransformation(transformation.toString());
                url.transformation(trans);
            }
            
            String transformedUrl = url.generate();
            log.debug("🖼️ Generated transformed URL: {}", transformedUrl);
            return transformedUrl;
        } catch (Exception e) {
            log.warn("⚠️ Không thể tạo transformed URL cho {}: {}", publicId, e.getMessage());
            return null;
        }
    }

    /**
     * Tạo thumbnail URL (300x300, auto format, auto quality)
     */
    public String generateThumbnailUrl(String publicId) {
        return generateTransformedUrl(publicId, 300, 300, "limit", "auto", "auto");
    }

    /**
     * Tạo medium size URL (800x800, auto format, auto quality)
     */
    public String generateMediumUrl(String publicId) {
        return generateTransformedUrl(publicId, 800, 800, "limit", "auto", "auto");
    }

    /**
     * Tạo large size URL (1200x1200, auto format, auto quality)
     */
    public String generateLargeUrl(String publicId) {
        return generateTransformedUrl(publicId, 1200, 1200, "limit", "auto", "auto");
    }
}
