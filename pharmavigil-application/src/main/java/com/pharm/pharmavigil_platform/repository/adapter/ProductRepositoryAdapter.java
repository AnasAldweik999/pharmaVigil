package com.pharm.pharmavigil_platform.repository.adapter;

import com.pharm.pharmavigil_platform.domain.Product;
import com.pharm.pharmavigil_platform.mapper.ProductMapper;
import com.pharm.pharmavigil_platform.repository.ProductRepository;
import com.pharm.pharmavigil_platform.repository.entities.ProductEntity;
import com.pharm.pharmavigil_platform.repository.jpa.JpaProductRepository;
import com.pharm.pharmavigil_platform.repository.listing.ProductListing;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository, ProductListing {

    private final JpaProductRepository jpaProductRepository;
    private final ProductMapper productMapper;

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return jpaProductRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id) {
        return jpaProductRepository.existsByNameIgnoreCaseAndIdNot(name, id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaProductRepository.existsById(id);
    }

    @Override
    public boolean existsByIdAndActiveTrue(UUID id) {
        return jpaProductRepository.existsByIdAndActiveTrue(id);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaProductRepository.findById(id).map(productMapper::toDomain);
    }

    @Override
    public Product save(Product product) {
        return productMapper.toDomain(jpaProductRepository.save(productMapper.toEntity(product)));
    }

    @Override
    public Page<Product> findAll(Specification<ProductEntity> spec, Pageable pageable) {
        return jpaProductRepository.findAll(spec, pageable).map(productMapper::toDomain);
    }
}
