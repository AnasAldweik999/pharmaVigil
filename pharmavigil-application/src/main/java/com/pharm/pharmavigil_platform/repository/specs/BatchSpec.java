package com.pharm.pharmavigil_platform.repository.specs;

import com.pharm.pharmavigil_platform.repository.entities.BatchEntity;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(params = "batchNo", path = "batchNo", spec = LikeIgnoreCase.class),
        @Spec(params = "productId", path = "product.id", spec = Equal.class),
        @Spec(params = "currentDepartmentId", path = "currentDepartment.id", spec = Equal.class),
        @Spec(params = "status", path = "status", spec = Equal.class),
        @Spec(params = "createdBy", path = "createdBy", spec = LikeIgnoreCase.class),
        @Spec(params = "createdFrom", path = "createdAt", spec = GreaterThanOrEqual.class),
        @Spec(params = "createdTo", path = "createdAt", spec = LessThanOrEqual.class),
        @Spec(params = "lastUpdatedBy", path = "lastUpdatedBy", spec = LikeIgnoreCase.class),
        @Spec(params = "lastUpdatedFrom", path = "lastUpdatedAt", spec = GreaterThanOrEqual.class),
        @Spec(params = "lastUpdatedTo", path = "lastUpdatedAt", spec = LessThanOrEqual.class)
})
public interface BatchSpec extends Specification<BatchEntity> {}
