package com.pharm.pharmavigil_platform.repository.specs;

import com.pharm.pharmavigil_platform.repository.entities.WorkReportEntity;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(params = "fromDate", path = "reportDate", spec = GreaterThanOrEqual.class),
        @Spec(params = "toDate", path = "reportDate", spec = LessThanOrEqual.class),
        @Spec(params = "shiftId", path = "shift.id", spec = Equal.class)
})
public interface WorkReportFilterSpec extends Specification<WorkReportEntity> {
}
