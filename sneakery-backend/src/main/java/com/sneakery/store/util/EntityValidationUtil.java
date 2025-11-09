package com.sneakery.store.util;

import com.sneakery.store.exception.ProductNotFoundException;
import com.sneakery.store.exception.ProductVariantNotFoundException;
import com.sneakery.store.exception.UserNotFoundException;
import org.springframework.data.repository.CrudRepository;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Utility class for entity validation and retrieval
 * Reduces code duplication in services
 */
public class EntityValidationUtil {

    /**
     * Find entity by ID or throw exception if not found
     * 
     * @param repository The repository to query
     * @param id The ID to find
     * @param exceptionSupplier Supplier for exception if not found
     * @param <T> Entity type
     * @param <ID> ID type
     * @return The found entity
     * @throws RuntimeException if entity not found
     */
    public static <T, ID> T findByIdOrThrow(
            CrudRepository<T, ID> repository,
            ID id,
            Supplier<? extends RuntimeException> exceptionSupplier
    ) {
        Objects.requireNonNull(id, "ID cannot be null");
        return repository.findById(id)
                .orElseThrow(exceptionSupplier);
    }

    /**
     * Find entity by ID or throw ProductNotFoundException
     */
    public static <T, ID> T findByIdOrThrowProductNotFound(
            CrudRepository<T, ID> repository,
            ID id
    ) {
        return findByIdOrThrow(repository, id, () -> new ProductNotFoundException(id.toString()));
    }

    /**
     * Find entity by ID or throw ProductVariantNotFoundException
     */
    public static <T, ID> T findByIdOrThrowVariantNotFound(
            CrudRepository<T, ID> repository,
            ID id
    ) {
        return findByIdOrThrow(repository, id, () -> new ProductVariantNotFoundException(id.toString()));
    }

    /**
     * Find entity by ID or throw UserNotFoundException
     */
    public static <T, ID> T findByIdOrThrowUserNotFound(
            CrudRepository<T, ID> repository,
            ID id
    ) {
        return findByIdOrThrow(repository, id, () -> new UserNotFoundException(id.toString()));
    }

    /**
     * Validate ID is not null
     * 
     * @param id The ID to validate
     * @param <ID> ID type
     * @return The validated ID
     * @throws NullPointerException if ID is null
     */
    public static <ID> ID requireNonNull(ID id) {
        return Objects.requireNonNull(id, "ID cannot be null");
    }

    /**
     * Validate ID is not null with custom message
     * 
     * @param id The ID to validate
     * @param message Custom error message
     * @param <ID> ID type
     * @return The validated ID
     * @throws NullPointerException if ID is null
     */
    public static <ID> ID requireNonNull(ID id, String message) {
        return Objects.requireNonNull(id, message);
    }
}

