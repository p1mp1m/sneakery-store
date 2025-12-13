package com.sneakery.store.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * Utility class để convert giữa priceRange (JSON string) và priceFrom/priceTo
 * Format JSON: {"from": 1000000, "to": 5000000}
 */
@Slf4j
public class PriceRangeConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * Convert priceFrom và priceTo thành JSON string để lưu vào database
     * @param priceFrom Giá từ (có thể null)
     * @param priceTo Giá đến (có thể null)
     * @return JSON string hoặc null nếu cả 2 đều null
     */
    public static String toJsonString(BigDecimal priceFrom, BigDecimal priceTo) {
        // Nếu cả 2 đều null thì trả về null
        if (priceFrom == null && priceTo == null) {
            return null;
        }

        try {
            PriceRange priceRange = new PriceRange();
            priceRange.setFrom(priceFrom);
            priceRange.setTo(priceTo);
            return objectMapper.writeValueAsString(priceRange);
        } catch (JsonProcessingException e) {
            log.error("Lỗi khi convert priceRange sang JSON: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Convert priceFrom và priceTo (dạng Integer/Double) thành JSON string
     * @param priceFrom Giá từ (có thể null)
     * @param priceTo Giá đến (có thể null)
     * @return JSON string hoặc null nếu cả 2 đều null
     */
    public static String toJsonString(Integer priceFrom, Integer priceTo) {
        BigDecimal from = priceFrom != null ? BigDecimal.valueOf(priceFrom) : null;
        BigDecimal to = priceTo != null ? BigDecimal.valueOf(priceTo) : null;
        return toJsonString(from, to);
    }

    /**
     * Parse JSON string thành PriceRange object
     * @param jsonString JSON string từ database
     * @return PriceRange object hoặc null nếu JSON không hợp lệ
     */
    public static PriceRange fromJsonString(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            log.debug("PriceRangeConverter: jsonString is null or empty");
            return null;
        }

        try {
            log.debug("PriceRangeConverter: Parsing JSON string: {}", jsonString);
            PriceRange priceRange = objectMapper.readValue(jsonString, PriceRange.class);
            log.debug("PriceRangeConverter: Parsed successfully - from: {}, to: {}", 
                priceRange.getFrom(), priceRange.getTo());
            return priceRange;
        } catch (JsonProcessingException e) {
            log.error("Lỗi khi parse priceRange từ JSON: {}. JSON string: {}", e.getMessage(), jsonString, e);
            return null;
        }
    }

    /**
     * Inner class để represent price range
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriceRange {
        private BigDecimal from;
        private BigDecimal to;

        public BigDecimal getFrom() {
            return from;
        }

        public void setFrom(BigDecimal from) {
            this.from = from;
        }

        public BigDecimal getTo() {
            return to;
        }

        public void setTo(BigDecimal to) {
            this.to = to;
        }

        public Integer getFromAsInteger() {
            return from != null ? from.intValue() : null;
        }

        public Integer getToAsInteger() {
            return to != null ? to.intValue() : null;
        }
    }
}

