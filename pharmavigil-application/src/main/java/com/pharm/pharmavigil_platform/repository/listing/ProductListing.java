package com.pharm.pharmavigil_platform.repository.listing;

import com.pharm.pharmavigil_platform.domain.Product;
import com.pharm.pharmavigil_platform.repository.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProductListing {
    Page<Product> findAll(Specification<ProductEntity> spec, Pageable pageable);
}
