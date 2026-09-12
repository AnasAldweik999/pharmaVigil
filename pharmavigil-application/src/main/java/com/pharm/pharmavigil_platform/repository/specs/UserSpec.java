package com.pharm.pharmavigil_platform.repository.specs;

import com.pharm.pharmavigil_platform.repository.entities.UserEntity;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(params = "name", path = "name", spec = LikeIgnoreCase.class),
        @Spec(params = "email", path = "email", spec = LikeIgnoreCase.class),
        @Spec(params = "username", path = "username", spec = LikeIgnoreCase.class),
        @Spec(params = "accountType", path = "accountType", spec = Equal.class),
        @Spec(params = "status", path = "status", spec = Equal.class),
        @Spec(params = "createdBy", path = "createdBy", spec = LikeIgnoreCase.class),
        @Spec(params = "lastUpdatedBy", path = "lastUpdatedBy", spec = LikeIgnoreCase.class)
})
public interface UserSpec extends Specification<UserEntity> {
}
