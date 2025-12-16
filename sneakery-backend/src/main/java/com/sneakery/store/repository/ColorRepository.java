package com.sneakery.store.repository;

import com.sneakery.store.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {

    Optional<Color> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);

    @Query("SELECT c FROM Color c WHERE c.isActive = true ORDER BY c.displayOrder ASC, c.name ASC")
    List<Color> findAllActive();

    @Query("SELECT c FROM Color c ORDER BY c.displayOrder ASC, c.name ASC")
    List<Color> findAllOrderByDisplayOrder();

    @Query("SELECT COUNT(pv) FROM ProductVariant pv WHERE pv.colorEntity.id = :colorId AND pv.deletedAt IS NULL")
    Long countVariantsUsingColorId(@Param("colorId") Integer colorId);
}
