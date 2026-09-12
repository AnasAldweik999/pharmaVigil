package com.pharm.pharmavigil_platform.usecases.product;

import com.pharm.pharmavigil_platform.domain.Product;
import com.pharm.pharmavigil_platform.repository.ProductRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class ToggleActiveProductUseCase {

    private final ValidatorChain<Product> validators;
    private final ProductRepository repository;
    private final IdentityProvider identityProvider;

    public ToggleActiveProductUseCase(ValidatorChain<Product> validators, ProductRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public Product execute(Product product) {
        log.info("Toggling active status for product with id '{}'", product.getId());
        validators.validate(product).throwExceptionIfViolated();
        product.setActive(!product.isActive());
        product.setLastUpdatedAt(Instant.now());
        product.setLastUpdatedBy(identityProvider.getCurrentUser().username());
        Product saved = repository.save(product);
        log.info("Product '{}' active status changed to '{}'", product.getId(), saved.isActive());
        return saved;
    }
}
