package com.sneakery.store.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sneakery.store.dto.VariantImageDto;
import com.sneakery.store.dto.VariantImageUploadDto;
import com.sneakery.store.entity.ProductVariant;
import com.sneakery.store.entity.VariantImage;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.ProductVariantRepository;
import com.sneakery.store.repository.VariantImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service quản lý ảnh variant — GỘP tất cả mapper + logic vào 1 file
 * Không dùng Impl, không dùng Mapper riêng.
 */
@Service
@RequiredArgsConstructor
public class VariantImageService {

    private final VariantImageRepository variantImageRepo;
    private final ProductVariantRepository variantRepo;
    private final Cloudinary cloudinary;

    /* ===========================
     *   GỘP MAPPER TRONG SERVICE
     * =========================== */
    private VariantImageDto toDto(VariantImage img) {
        return VariantImageDto.builder()
                .id(img.getId())
                .variantId(img.getVariant().getId())
                .imageUrl(img.getImageUrl())
                .altText(img.getAltText())
                .displayOrder(img.getDisplayOrder())
                .cloudinaryPublicId(img.getCloudinaryPublicId())
                .build();
    }


    /* ===========================
     *         GET IMAGES
     * =========================== */
    public List<VariantImageDto> getImagesByVariant(Long variantId) {
        ProductVariant variant = variantRepo.findById(variantId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Variant không tồn tại"));

        return variantImageRepo.findByVariantOrderByDisplayOrderAsc(variant)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    /* ===========================
     *        UPLOAD IMAGE
     * =========================== */
    public VariantImageDto uploadImage(VariantImageUploadDto dto) {

        // Tìm variant
        ProductVariant variant = variantRepo.findById(dto.getVariantId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Variant không tồn tại"));

        Long productId = variant.getProduct().getId();  // Lấy ID sản phẩm
        Long variantId = variant.getId();

        String folderPath = String.format("upload/sanpham/%d/%d", productId, variantId);

        String url;
        String publicId;

        try {
            var uploadResult = cloudinary.uploader().upload(
                    dto.getFile().getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderPath,       // Lưu theo folder chuẩn của bạn
                            "use_filename", true,
                            "unique_filename", true
                    )
            );

            url = (String) uploadResult.get("secure_url");
            publicId = (String) uploadResult.get("public_id");

        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi upload ảnh lên Cloudinary");
        }

        VariantImage img = VariantImage.builder()
                .variant(variant)
                .imageUrl(url)
                .altText(dto.getAltText())
                .displayOrder(dto.getDisplayOrder())
                .cloudinaryPublicId(publicId)
                .build();

        variantImageRepo.save(img);

        return toDto(img);
    }



    /* ===========================
     *       DELETE IMAGE
     * =========================== */
    public void deleteImage(Long imageId) {
        VariantImage img = variantImageRepo.findById(imageId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Ảnh không tồn tại"));

        // Xóa trên Cloudinary nếu có
        if (img.getCloudinaryPublicId() != null) {
            try {
                cloudinary.uploader().destroy(img.getCloudinaryPublicId(), ObjectUtils.emptyMap());
            } catch (Exception ignored) {}
        }

        variantImageRepo.delete(img);
    }


    /* ===========================
     *   XÓA TẤT CẢ ẢNH CỦA VARIANT
     * =========================== */
    public void deleteAllByVariant(Long variantId) {
        ProductVariant variant = variantRepo.findById(variantId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Variant không tồn tại"));

        List<VariantImage> images =
                variantImageRepo.findByVariantOrderByDisplayOrderAsc(variant);

        // Xóa trên Cloudinary trước
        for (VariantImage img : images) {
            if (img.getCloudinaryPublicId() != null) {
                try {
                    cloudinary.uploader().destroy(img.getCloudinaryPublicId(), ObjectUtils.emptyMap());
                } catch (Exception ignored) {}
            }
        }

        variantImageRepo.deleteByVariant(variant);
    }

}
