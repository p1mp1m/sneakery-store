package com.sneakery.store.controller;

import com.sneakery.store.dto.ShoeSoleDto;
import com.sneakery.store.service.ShoeSoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/shoe-soles")
@RequiredArgsConstructor
public class ShoeSoleController {

    private final ShoeSoleService shoeSoleService;

    @GetMapping
    public ResponseEntity<List<ShoeSoleDto>> getAll() {
        return ResponseEntity.ok(shoeSoleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShoeSoleDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(shoeSoleService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ShoeSoleDto> create(@RequestBody ShoeSoleDto dto) {
        return ResponseEntity.ok(shoeSoleService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShoeSoleDto> update(@PathVariable Integer id, @RequestBody ShoeSoleDto dto) {
        return ResponseEntity.ok(shoeSoleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        shoeSoleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
