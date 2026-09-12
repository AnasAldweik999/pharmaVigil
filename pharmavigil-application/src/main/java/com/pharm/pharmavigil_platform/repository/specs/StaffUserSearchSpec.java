package com.pharm.pharmavigil_platform.repository.specs;

import com.pharm.pharmavigil_platform.repository.entities.UserEntity;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@Or({
        @Spec(params = "search", path = "name", spec = LikeIgnoreCase.class),
        @Spec(params = "search", path = "email", spec = LikeIgnoreCase.class),
        @Spec(params = "search", path = "username", spec = LikeIgnoreCase.class)
})
public interface StaffUserSearchSpec extends Specification<UserEntity> {
}
