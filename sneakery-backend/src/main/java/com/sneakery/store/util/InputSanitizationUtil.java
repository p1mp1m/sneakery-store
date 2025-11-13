package com.sneakery.store.util;

import org.springframework.web.util.HtmlUtils;

/**
 * Utility class để sanitize user input và bảo vệ chống XSS attacks
 * 
 * <p>Class này cung cấp các phương thức để:
 * <ul>
 *   <li>Escape HTML characters để tránh XSS attacks</li>
 *   <li>Sanitize strings trước khi lưu vào database</li>
 *   <li>Clean input từ user trước khi xử lý</li>
 * </ul>
 * 
 * <p><b>Lưu ý:</b>
 * <ul>
 *   <li>Nên sử dụng cho tất cả user input trước khi hiển thị hoặc lưu</li>
 *   <li>Không nên sanitize password hoặc các trường cần giữ nguyên format</li>
 *   <li>HTML escaping sẽ chuyển các ký tự đặc biệt thành HTML entities</li>
 * </ul>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
public class InputSanitizationUtil {

    /**
     * Sanitize string input để tránh XSS attacks
     * Escape HTML characters thành HTML entities
     * 
     * <p>Ví dụ:
     * <ul>
     *   <li>"&lt;script&gt;alert('XSS')&lt;/script&gt;" → "&amp;lt;script&amp;gt;alert('XSS')&amp;lt;/script&amp;gt;"</li>
     *   <li>"Hello & World" → "Hello &amp; World"</li>
     * </ul>
     * 
     * @param input String cần sanitize (có thể null)
     * @return String đã được sanitize, hoặc null nếu input là null
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        // Escape HTML characters để tránh XSS
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }
        return HtmlUtils.htmlEscape(trimmed);
    }

    /**
     * Sanitize string input nhưng giữ nguyên whitespace (không trim)
     * 
     * @param input String cần sanitize (có thể null)
     * @return String đã được sanitize, hoặc null nếu input là null
     */
    public static String sanitizeKeepWhitespace(String input) {
        if (input == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(input);
    }

    /**
     * Sanitize string input cho search queries
     * Loại bỏ các ký tự đặc biệt có thể gây SQL injection (mặc dù đã dùng JPA)
     * 
     * @param input String cần sanitize (có thể null)
     * @return String đã được sanitize, hoặc null nếu input là null
     */
    public static String sanitizeForSearch(String input) {
        if (input == null) {
            return null;
        }
        // Escape HTML và trim
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }
        String sanitized = HtmlUtils.htmlEscape(trimmed);
        // Loại bỏ các ký tự đặc biệt nguy hiểm (mặc dù JPA đã protect, nhưng để an toàn thêm)
        sanitized = sanitized.replaceAll("[;'\"\\\\]", "");
        return sanitized;
    }

    /**
     * Sanitize email input
     * Chỉ escape HTML, không thay đổi format email
     * 
     * @param email Email cần sanitize (có thể null)
     * @return Email đã được sanitize, hoặc null nếu input là null
     */
    public static String sanitizeEmail(String email) {
        if (email == null) {
            return null;
        }
        // Chỉ trim, không escape vì email có thể chứa các ký tự đặc biệt hợp lệ
        return email.trim().toLowerCase();
    }

    /**
     * Sanitize phone number input
     * Chỉ giữ lại số và một số ký tự đặc biệt hợp lệ
     * 
     * @param phone Phone number cần sanitize (có thể null)
     * @return Phone number đã được sanitize, hoặc null nếu input là null
     */
    public static String sanitizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        // Chỉ giữ lại số, dấu +, dấu - và khoảng trắng
        return phone.trim().replaceAll("[^0-9+\\-\\s]", "");
    }

    /**
     * Kiểm tra xem string có chứa HTML tags không
     * 
     * @param input String cần kiểm tra (có thể null)
     * @return true nếu chứa HTML tags, false nếu không
     */
    public static boolean containsHtmlTags(String input) {
        if (input == null) {
            return false;
        }
        // Kiểm tra pattern HTML tags
        return input.matches(".*<[^>]+>.*");
    }

    /**
     * Loại bỏ tất cả HTML tags khỏi string
     * 
     * @param input String cần clean (có thể null)
     * @return String đã loại bỏ HTML tags, hoặc null nếu input là null
     */
    public static String stripHtmlTags(String input) {
        if (input == null) {
            return null;
        }
        // Loại bỏ tất cả HTML tags
        return input.replaceAll("<[^>]+>", "").trim();
    }
}

