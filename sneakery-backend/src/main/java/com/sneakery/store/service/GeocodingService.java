package com.sneakery.store.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Geocode địa chỉ -> trả về {lat, lon}
     */
    public double[] geocode(String address) {
        try {
//            String url = "https://nominatim.openstreetmap.org/search?q="
//                    + URI.create(address.replace(" ", "+"))
//                    + "&format=json&limit=1";
            String encoded = URLEncoder.encode(address, StandardCharsets.UTF_8);

            String url = "https://nominatim.openstreetmap.org/search?q="
                    + encoded
                    + "&format=json&limit=1&addressdetails=1";


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "SneakeryStore/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = objectMapper.readTree(response.body());

            if (root.isArray() && root.size() > 0) {
                JsonNode first = root.get(0);

                double lat = first.get("lat").asDouble();
                double lon = first.get("lon").asDouble();

                return new double[]{lat, lon};
            }

            log.warn("⚠️ Không tìm thấy toạ độ cho địa chỉ: {}", address);
            return new double[]{0, 0};

        } catch (Exception e) {
            log.error("❌ Geocoding error: {}", e.getMessage(), e);
            return new double[]{0, 0};
        }
    }
}
