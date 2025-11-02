package com.sneakery.store.service;

import com.sneakery.store.dto.MaterialDto;
import com.sneakery.store.entity.Material;
import com.sneakery.store.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ cho Chất liệu (Material)
 * - Bao gồm CRUD cơ bản.
 * - Không tách interface/impl mà gộp lại cho gọn.
 */
@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    /** Lấy tất cả chất liệu */
    public List<MaterialDto> getAll() {
        return materialRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** Lấy theo ID */
    public MaterialDto getById(Integer id) {
        Material entity = materialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chất liệu"));
        return toDto(entity);
    }

    /** Tạo mới chất liệu */
    public MaterialDto create(MaterialDto dto) {
        if (materialRepository.existsByName(dto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên chất liệu đã tồn tại");
        }
        Material entity = new Material();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setIsActive(true);
        return toDto(materialRepository.save(entity));
    }

    /** Cập nhật chất liệu */
    public MaterialDto update(Integer id, MaterialDto dto) {
        Material entity = materialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chất liệu"));
        BeanUtils.copyProperties(dto, entity, "id", "createdAt");
        entity.setUpdatedAt(LocalDateTime.now());
        return toDto(materialRepository.save(entity));
    }

    /** Xóa chất liệu */
    public void delete(Integer id) {
        Material entity = materialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chất liệu"));
        materialRepository.delete(entity);
    }

    /** Chuyển entity sang DTO */
    private MaterialDto toDto(Material entity) {
        MaterialDto dto = new MaterialDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
