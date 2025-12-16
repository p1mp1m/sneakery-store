package com.sneakery.store.service;

import com.sneakery.store.dto.ColorDto;
import com.sneakery.store.dto.ColorRequestDto;
import com.sneakery.store.entity.Color;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.exception.BusinessRuleException;
import com.sneakery.store.repository.ColorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ColorService {

    private final ColorRepository colorRepository;

    @Transactional(readOnly = true)
    public List<ColorDto> getAllColors() {
        return colorRepository.findAllOrderByDisplayOrder()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ColorDto> getActiveColors() {
        return colorRepository.findAllActive()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ColorDto getColorById(Integer id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy màu với ID: " + id));
        return convertToDto(color);
    }

    @Transactional(readOnly = true)
    public ColorDto getColorByName(String name) {
        Color color = colorRepository.findByName(name)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy màu: " + name));
        return convertToDto(color);
    }

    @Transactional
    public ColorDto createColor(ColorRequestDto requestDto) {
        if (colorRepository.existsByName(requestDto.getName().trim())) {
            throw new BusinessRuleException("Màu '" + requestDto.getName() + "' đã tồn tại");
        }

        Color color = Color.builder()
                .name(requestDto.getName().trim())
                .hexCode(requestDto.getHexCode())
                .displayOrder(requestDto.getDisplayOrder() != null ? requestDto.getDisplayOrder() : 0)
                .isActive(requestDto.getIsActive() != null ? requestDto.getIsActive() : true)
                .build();

        Color savedColor = colorRepository.save(color);
        log.info("Đã tạo màu mới: {} ({})", savedColor.getName(), savedColor.getHexCode());
        return convertToDto(savedColor);
    }

    @Transactional
    public ColorDto updateColor(Integer id, ColorRequestDto requestDto) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy màu với ID: " + id));

        if (colorRepository.existsByNameAndIdNot(requestDto.getName().trim(), id)) {
            throw new BusinessRuleException("Màu '" + requestDto.getName() + "' đã tồn tại");
        }

        color.setName(requestDto.getName().trim());
        color.setHexCode(requestDto.getHexCode());
        if (requestDto.getDisplayOrder() != null) {
            color.setDisplayOrder(requestDto.getDisplayOrder());
        }
        if (requestDto.getIsActive() != null) {
            color.setIsActive(requestDto.getIsActive());
        }

        Color updatedColor = colorRepository.save(color);
        log.info("Đã cập nhật màu ID {}: {} ({})", id, updatedColor.getName(), updatedColor.getHexCode());
        return convertToDto(updatedColor);
    }

    @Transactional
    public void deleteColor(Integer id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy màu với ID: " + id));

        Long variantCount = colorRepository.countVariantsUsingColorId(id);
        if (variantCount > 0) {
            throw new BusinessRuleException(
                    "Không thể xóa màu '" + color.getName() + "' vì đang được sử dụng bởi " + variantCount + " biến thể sản phẩm"
            );
        }

        colorRepository.delete(color);
        log.info("Đã xóa màu ID {}: {}", id, color.getName());
    }

    @Transactional
    public ColorDto toggleActive(Integer id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy màu với ID: " + id));

        color.setIsActive(!color.getIsActive());
        Color updatedColor = colorRepository.save(color);
        log.info("Đã {} màu ID {}: {}", color.getIsActive() ? "kích hoạt" : "vô hiệu hóa", id, color.getName());
        return convertToDto(updatedColor);
    }

    private ColorDto convertToDto(Color color) {
        return ColorDto.builder()
                .id(color.getId())
                .name(color.getName())
                .hexCode(color.getHexCode())
                .displayOrder(color.getDisplayOrder())
                .isActive(color.getIsActive())
                .createdAt(color.getCreatedAt())
                .updatedAt(color.getUpdatedAt())
                .build();
    }
}
