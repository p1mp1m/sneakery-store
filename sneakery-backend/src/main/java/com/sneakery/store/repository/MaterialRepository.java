package com.sneakery.store.repository;

import com.sneakery.store.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Integer> {
    Optional<Material> findBySlug(String slug);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}
