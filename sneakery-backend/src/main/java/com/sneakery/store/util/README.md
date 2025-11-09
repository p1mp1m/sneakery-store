# Utility Classes Documentation

## Overview
Các utility classes này được tạo để giảm code duplication và cải thiện code maintainability.

## 1. EntityValidationUtil

### Mục đích
Utility class cho entity validation và retrieval. Giảm code duplication trong các service methods.

### Cách sử dụng

#### Validate ID không null
```java
import com.sneakery.store.util.EntityValidationUtil;

// Validate ID không null
Long id = EntityValidationUtil.requireNonNull(requestId);

// Validate với custom message
Long id = EntityValidationUtil.requireNonNull(requestId, "Product ID cannot be null");
```

#### Find entity by ID hoặc throw exception
```java
// Generic method với custom exception
Product product = EntityValidationUtil.findByIdOrThrow(
    productRepository,
    productId,
    () -> new ProductNotFoundException(productId)
);

// Helper methods cho các exception phổ biến
Product product = EntityValidationUtil.findByIdOrThrowProductNotFound(
    productRepository,
    productId
);

User user = EntityValidationUtil.findByIdOrThrowUserNotFound(
    userRepository,
    userId
);
```

### Trước khi sử dụng
```java
Product product = productRepository.findById(Objects.requireNonNull(productId))
    .orElseThrow(() -> new ProductNotFoundException(productId));
```

### Sau khi sử dụng
```java
Product product = EntityValidationUtil.findByIdOrThrowProductNotFound(
    productRepository,
    productId
);
```

---

## 2. JsonUtil

### Mục đích
Utility class cho JSON operations. Giảm code duplication trong các service methods.

### Cách sử dụng

#### Parse JSON string to List of Strings
```java
import com.sneakery.store.util.JsonUtil;

// Parse JSON string to list
String imagesJson = "[\"url1\", \"url2\", \"url3\"]";
List<String> images = JsonUtil.parseJsonToStringList(imagesJson);

// Returns empty list if parsing fails or input is null/empty
List<String> images = JsonUtil.parseJsonToStringList(null); // Returns []
```

#### Convert List of Strings to JSON string
```java
// Convert list to JSON string
List<String> images = Arrays.asList("url1", "url2", "url3");
String imagesJson = JsonUtil.stringListToJson(images);

// Returns "[]" if input is null or empty
String imagesJson = JsonUtil.stringListToJson(null); // Returns "[]"
```

#### Get ObjectMapper instance
```java
// Get ObjectMapper instance for custom operations
ObjectMapper mapper = JsonUtil.getObjectMapper();
```

### Trước khi sử dụng
```java
private List<String> parseJsonImages(String imagesJson) {
    if (imagesJson == null || imagesJson.trim().isEmpty()) {
        return Collections.emptyList();
    }
    
    try {
        return objectMapper.readValue(imagesJson, new TypeReference<List<String>>() {});
    } catch (Exception e) {
        log.warn("Failed to parse images JSON: {}", imagesJson, e);
        return Collections.emptyList();
    }
}
```

### Sau khi sử dụng
```java
private List<String> parseJsonImages(String imagesJson) {
    return JsonUtil.parseJsonToStringList(imagesJson);
}
```

---

## 3. FileValidationUtil

### Mục đích
Utility class cho file upload validation. Validate file size và content type.

### Cách sử dụng

#### Validate image file
```java
import com.sneakery.store.util.FileValidationUtil;

// Validate file before processing
@PostMapping("/upload")
public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
    FileValidationUtil.validateImageFile(file);
    // Process file...
}
```

#### Throws BusinessRuleException nếu:
- File null hoặc empty
- File size > 5MB
- Content type không phải image (jpeg, png, jpg, gif, webp)

---

## Best Practices

1. **Sử dụng utility classes** thay vì duplicate code
2. **Import static** nếu sử dụng nhiều:
   ```java
   import static com.sneakery.store.util.EntityValidationUtil.requireNonNull;
   
   Long id = requireNonNull(requestId);
   ```
3. **Custom exception** nếu cần:
   ```java
   EntityValidationUtil.findByIdOrThrow(
       repository,
       id,
       () -> new CustomException("Custom message")
   );
   ```

---

## Future Improvements

1. **DtoConverter** - Generic converter cho Entity <-> DTO (nếu cần)
2. **PaginationUtil** - Utility cho pagination operations
3. **DateUtil** - Utility cho date/time operations
4. **StringUtil** - Utility cho string operations

