package com.sneakery.store.repository;

import com.sneakery.store.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer> {

    Optional<Size> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);

    @Query("SELECT s FROM Size s WHERE s.isActive = true ORDER BY s.displayOrder ASC, s.name ASC")
    List<Size> findAllActive();

    @Query("SELECT s FROM Size s ORDER BY s.displayOrder ASC, s.name ASC")
    List<Size> findAllOrderByDisplayOrder();

    @Query("SELECT COUNT(pv) FROM ProductVariant pv WHERE pv.sizeEntity.id = :sizeId AND pv.deletedAt IS NULL")
    Long countVariantsUsingSizeId(@Param("sizeId") Integer sizeId);
}
