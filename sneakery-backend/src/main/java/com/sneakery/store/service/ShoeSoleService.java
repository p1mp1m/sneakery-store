package com.sneakery.store.service;

import com.sneakery.store.dto.ShoeSoleDto;
import com.sneakery.store.entity.ShoeSole;
import com.sneakery.store.repository.ShoeSoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ cho Loại đế giày (Shoe Sole)
 * - Bao gồm CRUD cơ bản.
 * - Không tách interface/impl mà gộp lại cho gọn.
 */
@Service
@RequiredArgsConstructor
public class ShoeSoleService {

    private final ShoeSoleRepository shoeSoleRepository;

    /** Lấy danh sách tất cả loại đế */
    public List<ShoeSoleDto> getAll() {
        return shoeSoleRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** Lấy loại đế theo ID */
    public ShoeSoleDto getById(Integer id) {
        ShoeSole entity = shoeSoleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy loại đế giày"));
        return toDto(entity);
    }

    /** Tạo mới loại đế */
    public ShoeSoleDto create(ShoeSoleDto dto) {
        if (shoeSoleRepository.existsByName(dto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên loại đế giày đã tồn tại");
        }
        ShoeSole entity = new ShoeSole();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setIsActive(true);
        return toDto(shoeSoleRepository.save(entity));
    }

    /** Cập nhật loại đế */
    public ShoeSoleDto update(Integer id, ShoeSoleDto dto) {
        ShoeSole entity = shoeSoleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy loại đế giày"));
        BeanUtils.copyProperties(dto, entity, "id", "createdAt");
        entity.setUpdatedAt(LocalDateTime.now());
        return toDto(shoeSoleRepository.save(entity));
    }

    /** Xóa loại đế */
    public void delete(Integer id) {
        ShoeSole entity = shoeSoleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy loại đế giày"));
        shoeSoleRepository.delete(entity);
    }

    /** Chuyển entity sang DTO */
    private ShoeSoleDto toDto(ShoeSole entity) {
        ShoeSoleDto dto = new ShoeSoleDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
