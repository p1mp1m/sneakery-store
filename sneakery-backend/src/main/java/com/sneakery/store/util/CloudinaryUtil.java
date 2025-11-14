package com.sneakery.store.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Utility class cho Cloudinary operations
 * 
 * <p>Helper methods để tạo optimized URLs, thumbnails, và transformations
 */
@Slf4j
@Component
public class CloudinaryUtil {

    private final Cloudinary cloudinary;

    public CloudinaryUtil(@Autowired(required = false) Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Kiểm tra xem URL có phải là Cloudinary URL không
     */
    public boolean isCloudinaryUrl(String url) {
        return url != null && url.contains("cloudinary.com");
    }

    /**
     * Extract public ID từ Cloudinary URL
     * 
     * @param url Cloudinary URL
     * @return Public ID hoặc null nếu không phải Cloudinary URL
     */
    @Nullable
    public String extractPublicIdFromUrl(String url) {
        if (!isCloudinaryUrl(url)) {
            return null;
        }

        try {
            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex == -1) return null;

            String afterUpload = url.substring(uploadIndex + "/upload/".length());
            // Bỏ version vXXXXX/ nếu có
            afterUpload = afterUpload.replaceFirst("^v\\d+/", "");
            // Bỏ transformations nếu có (w_300,h_300,c_limit,q_auto,f_auto/)
            afterUpload = afterUpload.replaceFirst("^[^/]+/", "");
            // Bỏ extension
            afterUpload = afterUpload.replaceFirst("\\.[^.]+$", "");
            return afterUpload;
        } catch (Exception e) {
            log.warn("⚠️ Không thể extract public ID từ URL {}: {}", url, e.getMessage());
            return null;
        }
    }

    /**
     * Tạo optimized URL với transformations
     * 
     * @param originalUrl URL gốc (có thể là Cloudinary URL hoặc URL khác)
     * @param width Chiều rộng (null = không resize)
     * @param height Chiều cao (null = không resize)
     * @param crop Loại crop: "limit", "fill", "fit", "scale", "thumb" (null = không crop)
     * @param quality Chất lượng: "auto", "80", "90", "best" (null = auto)
     * @param format Định dạng: "auto", "webp", "jpg", "png" (null = auto)
     * @return Optimized URL hoặc original URL nếu không phải Cloudinary
     */
    @Nullable
    public String generateOptimizedUrl(String originalUrl, Integer width, Integer height,
                                       String crop, String quality, String format) {
        if (cloudinary == null || !isCloudinaryUrl(originalUrl)) {
            return originalUrl; // Return original nếu không phải Cloudinary
        }

        try {
            String publicId = extractPublicIdFromUrl(originalUrl);
            if (publicId == null) {
                return originalUrl;
            }

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

            String optimizedUrl = url.generate();
            log.debug("🖼️ Generated optimized URL: {}", optimizedUrl);
            return optimizedUrl;
        } catch (Exception e) {
            log.warn("⚠️ Không thể tạo optimized URL cho {}: {}", originalUrl, e.getMessage());
            return originalUrl; // Fallback to original
        }
    }

    /**
     * Tạo thumbnail URL (300x300, auto format, auto quality)
     */
    @Nullable
    public String generateThumbnailUrl(String originalUrl) {
        return generateOptimizedUrl(originalUrl, 300, 300, "limit", "auto", "auto");
    }

    /**
     * Tạo medium size URL (800x800, auto format, auto quality)
     */
    @Nullable
    public String generateMediumUrl(String originalUrl) {
        return generateOptimizedUrl(originalUrl, 800, 800, "limit", "auto", "auto");
    }

    /**
     * Tạo large size URL (1200x1200, auto format, auto quality)
     */
    @Nullable
    public String generateLargeUrl(String originalUrl) {
        return generateOptimizedUrl(originalUrl, 1200, 1200, "limit", "auto", "auto");
    }

    /**
     * Validate public ID format
     * 
     * @param publicId Public ID cần validate
     * @return true nếu hợp lệ
     */
    public boolean validatePublicId(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return false;
        }
        // Public ID không được chứa các ký tự đặc biệt
        // Cloudinary public ID thường là: folder/path/filename (không có extension)
        return publicId.matches("^[a-zA-Z0-9_/\\-]+$");
    }

    /**
     * Tạo responsive image srcset
     * 
     * @param originalUrl URL gốc
     * @param sizes Array các sizes: [300, 600, 1200]
     * @return Srcset string: "url1 300w, url2 600w, url3 1200w"
     */
    public String generateSrcset(String originalUrl, int[] sizes) {
        if (!isCloudinaryUrl(originalUrl) || sizes == null || sizes.length == 0) {
            return originalUrl;
        }

        StringBuilder srcset = new StringBuilder();
        for (int i = 0; i < sizes.length; i++) {
            String optimizedUrl = generateOptimizedUrl(originalUrl, sizes[i], sizes[i], "limit", "auto", "auto");
            if (optimizedUrl != null) {
                if (i > 0) srcset.append(", ");
                srcset.append(optimizedUrl).append(" ").append(sizes[i]).append("w");
            }
        }

        return srcset.toString();
    }

    /**
     * Tạo responsive srcset mặc định: 300w, 600w, 1200w
     */
    public String generateDefaultSrcset(String originalUrl) {
        return generateSrcset(originalUrl, new int[]{300, 600, 1200});
    }
}

