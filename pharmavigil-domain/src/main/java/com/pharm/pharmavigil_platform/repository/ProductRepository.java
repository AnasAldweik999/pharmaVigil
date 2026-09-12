package com.pharm.pharmavigil_platform.repository;

import com.pharm.pharmavigil_platform.domain.Product;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
    boolean existsById(UUID id);
    boolean existsByIdAndActiveTrue(UUID id);
    Optional<Product> findById(UUID id);
    Product save(Product product);
}
