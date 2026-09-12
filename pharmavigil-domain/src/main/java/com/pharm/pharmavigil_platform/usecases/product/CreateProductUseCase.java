package com.pharm.pharmavigil_platform.usecases.product;

import com.pharm.pharmavigil_platform.domain.Product;
import com.pharm.pharmavigil_platform.repository.ProductRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateProductUseCase {

    private final ValidatorChain<Product> validators;
    private final ProductRepository repository;
    private final IdentityProvider identityProvider;

    public CreateProductUseCase(ValidatorChain<Product> validators, ProductRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public Product execute(Product product) {
        log.info("Creating product with name '{}'", product.getName());
        validators.validate(product).throwExceptionIfViolated();
        product.setCreatedBy(identityProvider.getCurrentUser().username());
        Product created = repository.save(product);
        log.info("Product created successfully with id '{}'", created.getId());
        return created;
    }
}
