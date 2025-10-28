package com.sneakery.store.repository;

import com.sneakery.store.entity.ShoeSole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoeSoleRepository extends JpaRepository<ShoeSole, Integer> {
    Optional<ShoeSole> findBySlug(String slug);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}
