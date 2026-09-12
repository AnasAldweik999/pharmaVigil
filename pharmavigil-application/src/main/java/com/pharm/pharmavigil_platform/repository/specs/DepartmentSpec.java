package com.pharm.pharmavigil_platform.repository.specs;

import com.pharm.pharmavigil_platform.repository.entities.DepartmentEntity;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(params = "name", path = "name", spec = LikeIgnoreCase.class),
        @Spec(params = "hasOutputs", path = "hasOutputs", spec = Equal.class),
        @Spec(params = "showConsignee", path = "showConsignee", spec = Equal.class),
        @Spec(params = "terminalDepartment", path = "terminalDepartment", spec = Equal.class),
        @Spec(params = "createdBy", path = "createdBy", spec = LikeIgnoreCase.class),
        @Spec(params = "machineId", path = "machines.id", spec = Equal.class),
        @Spec(params = "machineName", path = "machines.name", spec = LikeIgnoreCase.class)
})
public interface DepartmentSpec extends Specification<DepartmentEntity> {}
