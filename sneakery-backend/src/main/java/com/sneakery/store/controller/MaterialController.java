package com.sneakery.store.controller;

import com.sneakery.store.dto.MaterialDto;
import com.sneakery.store.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping
    public ResponseEntity<List<MaterialDto>> getAll() {
        return ResponseEntity.ok(materialService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(materialService.getById(id));
    }

    @PostMapping
    public ResponseEntity<MaterialDto> create(@RequestBody MaterialDto dto) {
        return ResponseEntity.ok(materialService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialDto> update(@PathVariable Integer id, @RequestBody MaterialDto dto) {
        return ResponseEntity.ok(materialService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        materialService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
