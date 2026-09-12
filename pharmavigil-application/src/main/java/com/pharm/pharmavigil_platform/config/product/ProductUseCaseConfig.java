package com.pharm.pharmavigil_platform.config.product;

import com.pharm.pharmavigil_platform.domain.Product;
import com.pharm.pharmavigil_platform.repository.ProductRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.usecases.product.CreateProductUseCase;
import com.pharm.pharmavigil_platform.usecases.product.ToggleActiveProductUseCase;
import com.pharm.pharmavigil_platform.usecases.product.UpdateProductUseCase;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductUseCaseConfig {

    @Bean
    public CreateProductUseCase createProductUseCase(
            @Qualifier("productValidatorChain") ValidatorChain<Product> productValidatorChain,
            ProductRepository productRepository,
            IdentityProvider identityProvider) {
        return new CreateProductUseCase(productValidatorChain, productRepository, identityProvider);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase(
            @Qualifier("productUpdateValidatorChain") ValidatorChain<Product> productUpdateValidatorChain,
            ProductRepository productRepository,
            IdentityProvider identityProvider) {
        return new UpdateProductUseCase(productUpdateValidatorChain, productRepository, identityProvider);
    }

    @Bean
    public ToggleActiveProductUseCase toggleActiveProductUseCase(
            @Qualifier("productToggleActiveValidatorChain") ValidatorChain<Product> productToggleActiveValidatorChain,
            ProductRepository productRepository,
            IdentityProvider identityProvider) {
        return new ToggleActiveProductUseCase(productToggleActiveValidatorChain, productRepository, identityProvider);
    }
}
