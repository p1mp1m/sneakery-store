package com.sneakery.store.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

/**
 * Config: CloudinaryConfig
 * ------------------------
 * Cấu hình Cloudinary bean để upload hình ảnh/asset.
 * Nếu không có cấu hình đầy đủ, bean sẽ là null và hệ thống sẽ dùng local storage.
 */
@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    @Bean
    @Nullable
    public Cloudinary cloudinary() {
        // Kiểm tra nếu có giá trị placeholder hoặc rỗng thì không tạo bean
        if (cloudName == null || cloudName.isBlank() || 
            cloudName.equals("YOUR_CLOUDINARY_CLOUD_NAME") ||
            apiKey == null || apiKey.isBlank() || 
            apiKey.equals("YOUR_CLOUDINARY_API_KEY") ||
            apiSecret == null || apiSecret.isBlank() || 
            apiSecret.equals("YOUR_CLOUDINARY_API_SECRET")) {
            return null;
        }
        
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }
}
