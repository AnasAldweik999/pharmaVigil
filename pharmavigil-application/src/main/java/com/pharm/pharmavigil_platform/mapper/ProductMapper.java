package com.pharm.pharmavigil_platform.mapper;

import com.pharm.pharmavigil_platform.domain.Product;
import com.pharm.pharmavigil_platform.repository.entities.ProductEntity;
import com.pharm.pharmavigil_platform.resources.product.CreateProductRequest;
import com.pharm.pharmavigil_platform.resources.product.ProductResponse;
import com.pharm.pharmavigil_platform.resources.product.UpdateProductRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = StringMapper.class)
public interface ProductMapper {

    @Mapping(target = "name", source = "name", qualifiedByName = "trim")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    Product toDomain(CreateProductRequest request);

    @Mapping(target = "name", source = "name", qualifiedByName = "trim")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    Product toDomain(UpdateProductRequest request);

    Product toDomain(ProductEntity entity);

    ProductEntity toEntity(Product product);

    ProductResponse toResponse(Product product);
}
