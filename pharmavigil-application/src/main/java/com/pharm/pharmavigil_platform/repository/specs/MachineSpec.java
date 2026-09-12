package com.pharm.pharmavigil_platform.repository.specs;

import com.pharm.pharmavigil_platform.repository.entities.MachineEntity;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(params = "name", path = "name", spec = LikeIgnoreCase.class),
        @Spec(params = "createdBy", path = "createdBy", spec = LikeIgnoreCase.class),
        @Spec(params = "departmentId", path = "department.id", spec = Equal.class)
})
public interface MachineSpec extends Specification<MachineEntity> {}
