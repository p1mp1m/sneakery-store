package com.sneakery.store.service;

import com.sneakery.store.dto.BrandDto;
import com.sneakery.store.entity.Brand;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandDto createBrand(BrandDto brandDto) {
        Brand brand = convertToEntity(brandDto);
        Brand savedBrand = brandRepository.save(brand);
        return convertToDto(savedBrand);
    }

    public BrandDto getBrandById(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu"));
        return convertToDto(brand);
    }

    public List<BrandDto> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BrandDto updateBrand(Integer id, BrandDto brandDto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu"));
        
        brand.setName(brandDto.getName());
        brand.setSlug(brandDto.getSlug());
        brand.setLogoUrl(brandDto.getLogoUrl()); // Giả sử BrandDto có logoUrl
        
        Brand updatedBrand = brandRepository.save(brand);
        return convertToDto(updatedBrand);
    }

    public void deleteBrand(Integer id) {
        if (!brandRepository.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu");
        }

        brandRepository.deleteById(id);
    }

    // --- Mapper ---
    private BrandDto convertToDto(Brand brand) {
        return BrandDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .build();
    }
    
    private Brand convertToEntity(BrandDto dto) {
        Brand brand = new Brand();
        brand.setName(dto.getName());
        brand.setSlug(dto.getSlug());
        // brand.setLogoUrl(dto.getLogoUrl());
        return brand;
    }
}