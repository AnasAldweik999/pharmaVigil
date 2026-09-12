package com.pharm.pharmavigil_platform.config.product;

import com.pharm.pharmavigil_platform.domain.Product;
import com.pharm.pharmavigil_platform.repository.ProductRepository;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import com.pharm.pharmavigil_platform.validators.product.ProductExistsValidator;
import com.pharm.pharmavigil_platform.validators.product.ProductNameNotTakenByOthersValidator;
import com.pharm.pharmavigil_platform.validators.product.ProductNameValidator;
import com.pharm.pharmavigil_platform.validators.product.ProductUniquenessValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ProductValidatorsConfig {

    @Bean
    public ProductNameValidator productNameValidator() {
        return new ProductNameValidator();
    }

    @Bean
    public ProductUniquenessValidator productUniquenessValidator(ProductRepository productRepository) {
        return new ProductUniquenessValidator(productRepository);
    }

    @Bean
    public ProductNameNotTakenByOthersValidator productNameNotTakenByOthersValidator(ProductRepository productRepository) {
        return new ProductNameNotTakenByOthersValidator(productRepository);
    }

    @Bean
    public ProductExistsValidator productExistsValidator(ProductRepository productRepository) {
        return new ProductExistsValidator(productRepository);
    }

    @Bean("productValidatorChain")
    public ValidatorChain<Product> productValidatorChain(
            ProductNameValidator productNameValidator,
            ProductUniquenessValidator productUniquenessValidator) {
        return new ValidatorChain<>(List.of(productNameValidator, productUniquenessValidator));
    }

    @Bean("productUpdateValidatorChain")
    public ValidatorChain<Product> productUpdateValidatorChain(
            ProductExistsValidator productExistsValidator,
            ProductNameValidator productNameValidator,
            ProductNameNotTakenByOthersValidator productNameNotTakenByOthersValidator) {
        return new ValidatorChain<>(List.of(productExistsValidator, productNameValidator, productNameNotTakenByOthersValidator));
    }

    @Bean("productToggleActiveValidatorChain")
    public ValidatorChain<Product> productToggleActiveValidatorChain(ProductExistsValidator productExistsValidator) {
        return new ValidatorChain<>(List.of(productExistsValidator));
    }
}
