package com.pharm.pharmavigil_platform.controller.supervisor;

import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.repository.specs.ProductSpec;
import com.pharm.pharmavigil_platform.resources.product.CreateProductRequest;
import com.pharm.pharmavigil_platform.resources.product.ProductResponse;
import com.pharm.pharmavigil_platform.resources.product.UpdateProductRequest;
import com.pharm.pharmavigil_platform.security.RequiresRole;
import com.pharm.pharmavigil_platform.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/supervisor/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @RequiresRole({UserRole.PRODUCTS_MANAGER, UserRole.DASHBOARD_VIEWER, UserRole.DEPARTMENTS_MANAGER, UserRole.BATCH_TRACKING_MANAGER})
    public ResponseEntity<Page<ProductResponse>> getAll(ProductSpec spec, Pageable pageable) {
        return ResponseEntity.ok(productService.getAll(spec, pageable));
    }

    @GetMapping("/{id}")
    @RequiresRole({UserRole.PRODUCTS_MANAGER, UserRole.DASHBOARD_VIEWER})
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    @RequiresRole(UserRole.PRODUCTS_MANAGER)
    public ResponseEntity<ProductResponse> create(@RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @PutMapping("/{id}")
    @RequiresRole(UserRole.PRODUCTS_MANAGER)
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id, @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @PatchMapping("/{id}/active")
    @RequiresRole(UserRole.PRODUCTS_MANAGER)
    public ResponseEntity<ProductResponse> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.toggleActive(id));
    }
}
