package com.sneakery.store.controller;

import com.sneakery.store.dto.ProductCardDto;
import com.sneakery.store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; // QUAN TRỌNG
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

@RestController // QUAN TRỌNG: Đảm bảo bạn có dòng này
@RequestMapping("/api/products") // QUAN TRỌNG: Và dòng này
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductService productService;

//    @GetMapping
//    public ResponseEntity<List<ProductCardDto>> getAllProducts() {
//        List<ProductCardDto> products = productService.getAllProductsForCard();
//        return ResponseEntity.ok(products);
//    }

    // Sửa lại endpoint để nhận tham số page và size
    @GetMapping
    public ResponseEntity<Page<ProductCardDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) { // Mặc định hiển thị 8 sản phẩm/trang
        Page<ProductCardDto> products = productService.getAllProductsForCard(page, size);
        return ResponseEntity.ok(products);
    }
}