package com.pharm.pharmavigil_platform.mapper;

import com.pharm.pharmavigil_platform.domain.*;
import com.pharm.pharmavigil_platform.repository.entities.*;
import com.pharm.pharmavigil_platform.resources.supervisor.SupervisorWorkReportListResponse;
import com.pharm.pharmavigil_platform.resources.workreport.CreateWorkReportRequest;
import com.pharm.pharmavigil_platform.resources.workreport.WorkReportResponse;
import com.pharm.pharmavigil_platform.util.DurationFormatter;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = DurationFormatter.class)
public interface WorkReportMapper {

    WorkReport toDomain(CreateWorkReportRequest request);

    WorkReportMachine fromMachineRequest(CreateWorkReportRequest.MachineEntryRequest request);

    @Mapping(target = "deviation", source = "quality.deviation")
    @Mapping(target = "deviationDetails", source = "quality.deviationDetails")
    @Mapping(target = "hold", source = "quality.hold")
    @Mapping(target = "holdDetails", source = "quality.holdDetails")
    WorkReportProduct fromProductRequest(CreateWorkReportRequest.ProductEntryRequest request);

    WorkReportProductStop fromStopRequest(CreateWorkReportRequest.StopRequest request);

    @AfterMapping
    default void setDomainBackRefs(CreateWorkReportRequest source, @MappingTarget WorkReport report) {
        if (report.getMachines() == null) return;
        report.getMachines().forEach(m -> {
            m.setWorkReport(report);
            if (m.getProducts() != null) {
                m.getProducts().forEach(p -> {
                    p.setWorkReportMachine(m);
                    if (p.getStops() != null) p.getStops().forEach(s -> s.setProduct(p));
                });
            }
        });
    }

    @Mapping(target = "staffUser", ignore = true)
    @Mapping(target = "shiftId", source = "shift.id")
    WorkReport toDomain(WorkReportEntity entity);

    @Mapping(target = "workReport", ignore = true)
    @Mapping(target = "machineId", source = "machine.id")
    @Mapping(target = "departmentId", source = "department.id")
    WorkReportMachine toMachineDomain(WorkReportMachineEntity entity);

    @Mapping(target = "workReportMachine", ignore = true)
    @Mapping(target = "productId", source = "product.id")
    WorkReportProduct toProductDomain(WorkReportProductEntity entity);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "stopTypeId", source = "stopType.id")
    WorkReportProductStop toStopDomain(WorkReportProductStopEntity entity);

    @Mapping(target = "staffUser", ignore = true)
    @Mapping(target = "shift", ignore = true)
    WorkReportEntity toEntity(WorkReport workReport);

    @Mapping(target = "workReport", ignore = true)
    @Mapping(target = "machine", ignore = true)
    @Mapping(target = "department", ignore = true)
    WorkReportMachineEntity toMachineEntity(WorkReportMachine machine);

    @Mapping(target = "workReportMachine", ignore = true)
    @Mapping(target = "product", ignore = true)
    WorkReportProductEntity toProductEntity(WorkReportProduct product);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "stopType", ignore = true)
    WorkReportProductStopEntity toStopEntity(WorkReportProductStop stop);

    // Domain → Response
    @Mapping(target = "staffId", source = "staffUser.id")
    @Mapping(target = "staffName", source = "staffUser.name")
    @Mapping(target = "staffUsername", source = "staffUser.username")
    @Mapping(target = "staffEmail", source = "staffUser.email")
    WorkReportResponse toResponse(WorkReport report);
    WorkReportResponse.MachineEntryResponse toMachineResponse(WorkReportMachine machine);

    @Mapping(target = "quality.deviation", source = "deviation")
    @Mapping(target = "quality.deviationDetails", source = "deviationDetails")
    @Mapping(target = "quality.hold", source = "hold")
    @Mapping(target = "quality.holdDetails", source = "holdDetails")
    WorkReportResponse.ProductEntryResponse toProductResponse(WorkReportProduct product);

    WorkReportResponse.StopResponse toStopResponse(WorkReportProductStop stop);

    // Entity → Response
    // shiftName prefers the live Shift's current name over the denormalized snapshot column, so a
    // rename is reflected on every work report that references it. The snapshot fallback is dead
    // code today (shift is a mandatory relation) but keeps shift/machine/product/stopType on one
    // identical, defensible pattern.
    @Mapping(target = "staffId", source = "staffUser.id")
    @Mapping(target = "staffName", source = "staffUser.name")
    @Mapping(target = "staffUsername", source = "staffUser.username")
    @Mapping(target = "staffEmail", source = "staffUser.email")
    @Mapping(target = "shiftName", expression = "java(entity.getShift() != null ? entity.getShift().getName() : entity.getShiftName())")
    WorkReportResponse toResponse(WorkReportEntity entity);

    // machineName / departmentName: same live-preferred, snapshot-fallback pattern as shiftName
    // (see toResponse(WorkReportEntity)) — department is a nullable relation, so both the live
    // name and the snapshot fallback can be null when no department is set.
    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", expression = "java(entity.getDepartment() != null ? entity.getDepartment().getName() : entity.getDepartmentName())")
    @Mapping(target = "machineName", expression = "java(entity.getMachine() != null ? entity.getMachine().getName() : entity.getMachineName())")
    WorkReportResponse.MachineEntryResponse toMachineResponse(WorkReportMachineEntity entity);

    // productName prefers the live Product's current name over the denormalized snapshot column,
    // so a rename in the product catalog is reflected on every work report that references it.
    // The snapshot is a real fallback here — product is a nullable relation (legacy pre-refactor rows).
    @Mapping(target = "quality.deviation", source = "deviation")
    @Mapping(target = "quality.deviationDetails", source = "deviationDetails")
    @Mapping(target = "quality.hold", source = "hold")
    @Mapping(target = "quality.holdDetails", source = "holdDetails")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", expression = "java(entity.getProduct() != null ? entity.getProduct().getName() : entity.getProductName())")
    WorkReportResponse.ProductEntryResponse toProductResponse(WorkReportProductEntity entity);

    // stopTypeName: same live-preferred pattern as productName — stopType is a nullable relation,
    // so the snapshot fallback is real (legacy pre-refactor rows).
    @Mapping(target = "stopTypeName", expression = "java(entity.getStopType() != null ? entity.getStopType().getName() : entity.getStopTypeName())")
    WorkReportResponse.StopResponse toStopResponse(WorkReportProductStopEntity entity);

    @Mapping(target = "staffName", source = "staffUser.name")
    @Mapping(target = "staffUsername", source = "staffUser.username")
    @Mapping(target = "staffEmail", source = "staffUser.email")
    @Mapping(target = "shiftName", expression = "java(entity.getShift() != null ? entity.getShift().getName() : entity.getShiftName())")
    SupervisorWorkReportListResponse toListResponse(WorkReportEntity entity);

    @AfterMapping
    default void setEntityBackRefs(@MappingTarget WorkReportEntity entity) {
        entity.getMachines().forEach(m -> {
            m.setWorkReport(entity);
            m.getProducts().forEach(p -> {
                p.setWorkReportMachine(m);
                p.getStops().forEach(s -> s.setProduct(p));
            });
        });
    }
}
