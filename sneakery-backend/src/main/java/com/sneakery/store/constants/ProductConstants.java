package com.sneakery.store.constants;

/**
 * Constants cho Product Management
 * 
 * <p>Chứa các giá trị cố định được sử dụng trong module quản lý sản phẩm,
 * tránh hardcode magic numbers/strings trong code.
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
public final class ProductConstants {

    private ProductConstants() {
        // Utility class - prevent instantiation
    }

    /**
     * Low stock threshold - Số lượng tồn kho tối thiểu để cảnh báo
     * Variants có stock <= LOW_STOCK_THRESHOLD sẽ được đánh dấu là "low stock"
     */
    public static final int LOW_STOCK_THRESHOLD = 10;

    /**
     * Default page size cho pagination
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * Maximum number of images per product
     */
    public static final int MAX_IMAGES_PER_PRODUCT = 10;

    /**
     * Maximum file size for image upload (in bytes)
     * 5MB = 5 * 1024 * 1024 bytes
     */
    public static final long MAX_IMAGE_FILE_SIZE = 5 * 1024 * 1024L;

    /**
     * Allowed image file types
     */
    public static final String[] ALLOWED_IMAGE_TYPES = {
        "image/jpeg",
        "image/jpg", 
        "image/png",
        "image/webp"
    };

    /**
     * Allowed image file extensions
     */
    public static final String[] ALLOWED_IMAGE_EXTENSIONS = {
        ".jpg",
        ".jpeg",
        ".png",
        ".webp"
    };

    /**
     * Stock level constants
     */
    public static final class StockLevel {
        public static final String IN_STOCK = "in_stock";
        public static final String LOW_STOCK = "low_stock";
        public static final String OUT_OF_STOCK = "out_of_stock";
        public static final String ALL = "all";
        
        private StockLevel() {}
    }

    /**
     * Status constants
     */
    public static final class Status {
        public static final String ACTIVE = "active";
        public static final String INACTIVE = "inactive";
        public static final String ALL = "all";
        
        private Status() {}
    }

    /**
     * Sort field constants
     */
    public static final class SortField {
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String STOCK = "stock";
        public static final String ID = "id";
        public static final String CREATED_AT = "created_at";
        
        private SortField() {}
    }

    /**
     * Sort direction constants
     */
    public static final class SortDirection {
        public static final String ASC = "asc";
        public static final String DESC = "desc";
        
        private SortDirection() {}
    }

    /**
     * Bulk update action constants
     */
    public static final class BulkUpdateAction {
        public static final String UPDATE_STATUS = "UPDATE_STATUS";
        public static final String UPDATE_BRAND = "UPDATE_BRAND";
        public static final String ADD_CATEGORY = "ADD_CATEGORY";
        public static final String REMOVE_CATEGORY = "REMOVE_CATEGORY";
        
        private BulkUpdateAction() {}
    }

    /**
     * Product code prefix
     */
    public static final String PRODUCT_CODE_PREFIX = "SP";

    /**
     * Duplicate suffix for product name
     */
    public static final String DUPLICATE_NAME_SUFFIX = " (Copy)";

    /**
     * Duplicate suffix for SKU
     */
    public static final String DUPLICATE_SKU_SUFFIX = "-COPY";
}

