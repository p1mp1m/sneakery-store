package com.sneakery.store.service;

import com.sneakery.store.dto.SizeDto;
import com.sneakery.store.dto.SizeRequestDto;
import com.sneakery.store.entity.Size;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.exception.BusinessRuleException;
import com.sneakery.store.repository.SizeRepository;
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
public class SizeService {

    private final SizeRepository sizeRepository;

    @Transactional(readOnly = true)
    public List<SizeDto> getAllSizes() {
        return sizeRepository.findAllOrderByDisplayOrder()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SizeDto> getActiveSizes() {
        return sizeRepository.findAllActive()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SizeDto getSizeById(Integer id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy size với ID: " + id));
        return convertToDto(size);
    }

    @Transactional(readOnly = true)
    public SizeDto getSizeByName(String name) {
        Size size = sizeRepository.findByName(name)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy size: " + name));
        return convertToDto(size);
    }

    @Transactional
    public SizeDto createSize(SizeRequestDto requestDto) {
        if (sizeRepository.existsByName(requestDto.getName().trim())) {
            throw new BusinessRuleException("Size '" + requestDto.getName() + "' đã tồn tại");
        }

        Size size = Size.builder()
                .name(requestDto.getName().trim())
                .displayOrder(requestDto.getDisplayOrder() != null ? requestDto.getDisplayOrder() : 0)
                .isActive(requestDto.getIsActive() != null ? requestDto.getIsActive() : true)
                .build();

        Size savedSize = sizeRepository.save(size);
        log.info("Đã tạo size mới: {}", savedSize.getName());
        return convertToDto(savedSize);
    }

    @Transactional
    public SizeDto updateSize(Integer id, SizeRequestDto requestDto) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy size với ID: " + id));

        if (sizeRepository.existsByNameAndIdNot(requestDto.getName().trim(), id)) {
            throw new BusinessRuleException("Size '" + requestDto.getName() + "' đã tồn tại");
        }

        size.setName(requestDto.getName().trim());
        if (requestDto.getDisplayOrder() != null) {
            size.setDisplayOrder(requestDto.getDisplayOrder());
        }
        if (requestDto.getIsActive() != null) {
            size.setIsActive(requestDto.getIsActive());
        }

        Size updatedSize = sizeRepository.save(size);
        log.info("Đã cập nhật size ID {}: {}", id, updatedSize.getName());
        return convertToDto(updatedSize);
    }

    @Transactional
    public void deleteSize(Integer id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy size với ID: " + id));

        Long variantCount = sizeRepository.countVariantsUsingSizeId(id);
        if (variantCount > 0) {
            throw new BusinessRuleException(
                    "Không thể xóa size '" + size.getName() + "' vì đang được sử dụng bởi " + variantCount + " biến thể sản phẩm"
            );
        }

        sizeRepository.delete(size);
        log.info("Đã xóa size ID {}: {}", id, size.getName());
    }

    @Transactional
    public SizeDto toggleActive(Integer id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy size với ID: " + id));

        size.setIsActive(!size.getIsActive());
        Size updatedSize = sizeRepository.save(size);
        log.info("Đã {} size ID {}: {}", size.getIsActive() ? "kích hoạt" : "vô hiệu hóa", id, size.getName());
        return convertToDto(updatedSize);
    }

    private SizeDto convertToDto(Size size) {
        return SizeDto.builder()
                .id(size.getId())
                .name(size.getName())
                .displayOrder(size.getDisplayOrder())
                .isActive(size.getIsActive())
                .createdAt(size.getCreatedAt())
                .updatedAt(size.getUpdatedAt())
                .build();
    }
}
