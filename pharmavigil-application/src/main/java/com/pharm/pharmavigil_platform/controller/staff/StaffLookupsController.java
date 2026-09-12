package com.pharm.pharmavigil_platform.controller.staff;

import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.repository.specs.DepartmentSpec;
import com.pharm.pharmavigil_platform.repository.specs.MachineSpec;
import com.pharm.pharmavigil_platform.repository.specs.ProductSpec;
import com.pharm.pharmavigil_platform.repository.specs.ShiftSpec;
import com.pharm.pharmavigil_platform.repository.specs.StopTypeSpec;
import com.pharm.pharmavigil_platform.resources.department.DepartmentResponse;
import com.pharm.pharmavigil_platform.resources.machine.MachineResponse;
import com.pharm.pharmavigil_platform.resources.product.ProductResponse;
import com.pharm.pharmavigil_platform.resources.shift.ShiftResponse;
import com.pharm.pharmavigil_platform.resources.stoptype.StopTypeResponse;
import com.pharm.pharmavigil_platform.security.RequiresRole;
import com.pharm.pharmavigil_platform.service.DepartmentService;
import com.pharm.pharmavigil_platform.service.MachineService;
import com.pharm.pharmavigil_platform.service.ProductService;
import com.pharm.pharmavigil_platform.service.ShiftService;
import com.pharm.pharmavigil_platform.service.StopTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/staff/reference")
@RequiredArgsConstructor
public class StaffLookupsController {

    private final ShiftService shiftService;
    private final MachineService machineService;
    private final StopTypeService stopTypeService;
    private final DepartmentService departmentService;
    private final ProductService productService;

    @GetMapping("/shifts")
    @RequiresRole(UserRole.FIELD_REPORTER)
    public ResponseEntity<Page<ShiftResponse>> shifts(ShiftSpec spec, Pageable pageable) {
        return ResponseEntity.ok(shiftService.getActive(spec, pageable));
    }

    @GetMapping("/machines")
    @RequiresRole({UserRole.FIELD_REPORTER, UserRole.BATCH_LOG_REPORTER})
    public ResponseEntity<Page<MachineResponse>> machines(MachineSpec spec, Pageable pageable) {
        return ResponseEntity.ok(machineService.getActive(spec, pageable));
    }

    @GetMapping("/stop-types")
    @RequiresRole(UserRole.FIELD_REPORTER)
    public ResponseEntity<Page<StopTypeResponse>> stopTypes(StopTypeSpec spec, Pageable pageable) {
        return ResponseEntity.ok(stopTypeService.getActive(spec, pageable));
    }

    @GetMapping("/departments")
    @RequiresRole({UserRole.FIELD_REPORTER, UserRole.BATCH_LOG_REPORTER})
    public ResponseEntity<Page<DepartmentResponse>> departments(DepartmentSpec spec, Pageable pageable) {
        return ResponseEntity.ok(departmentService.getActive(spec, pageable));
    }

    @GetMapping("/products")
    @RequiresRole({UserRole.FIELD_REPORTER, UserRole.BATCH_LOG_REPORTER})
    public ResponseEntity<Page<ProductResponse>> products(ProductSpec spec, Pageable pageable) {
        return ResponseEntity.ok(productService.getActive(spec, pageable));
    }
}
