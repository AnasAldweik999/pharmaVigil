package com.pharm.pharmavigil_platform.service;

import com.pharm.pharmavigil_platform.domain.Product;
import com.pharm.pharmavigil_platform.exception.ResourceNotFoundException;
import com.pharm.pharmavigil_platform.mapper.ProductMapper;
import com.pharm.pharmavigil_platform.repository.adapter.ProductRepositoryAdapter;
import com.pharm.pharmavigil_platform.repository.entities.ProductEntity;
import com.pharm.pharmavigil_platform.repository.specs.ProductSpec;
import com.pharm.pharmavigil_platform.resources.product.CreateProductRequest;
import com.pharm.pharmavigil_platform.resources.product.ProductResponse;
import com.pharm.pharmavigil_platform.resources.product.UpdateProductRequest;
import com.pharm.pharmavigil_platform.usecases.product.CreateProductUseCase;
import com.pharm.pharmavigil_platform.usecases.product.ToggleActiveProductUseCase;
import com.pharm.pharmavigil_platform.usecases.product.UpdateProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepositoryAdapter adapter;
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final ToggleActiveProductUseCase toggleActiveProductUseCase;
    private final ProductMapper productMapper;

    public Page<ProductResponse> getAll(ProductSpec spec, Pageable pageable) {
        return adapter.findAll(spec, pageable).map(productMapper::toResponse);
    }

    public Page<ProductResponse> getActive(ProductSpec spec, Pageable pageable) {
        Specification<ProductEntity> combined = spec != null ? spec.and(isActive()) : Specification.where(isActive());
        return adapter.findAll(combined, pageable).map(productMapper::toResponse);
    }

    public ProductResponse getById(UUID id) {
        return adapter.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Product product = productMapper.toDomain(request);
        Product created = createProductUseCase.execute(product);
        return productMapper.toResponse(created);
    }

    @Transactional
    public ProductResponse update(UUID id, UpdateProductRequest request) {
        Product existing = findOrThrow(id);
        Product product = productMapper.toDomain(request);
        product.setId(id);
        product.setActive(existing.isActive());
        product.setCreatedAt(existing.getCreatedAt());
        product.setCreatedBy(existing.getCreatedBy());
        Product updated = updateProductUseCase.execute(product);
        return productMapper.toResponse(updated);
    }

    @Transactional
    public ProductResponse toggleActive(UUID id) {
        Product product = findOrThrow(id);
        Product saved = toggleActiveProductUseCase.execute(product);
        return productMapper.toResponse(saved);
    }

    private Product findOrThrow(UUID id) {
        return adapter.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    private Specification<ProductEntity> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }
}
