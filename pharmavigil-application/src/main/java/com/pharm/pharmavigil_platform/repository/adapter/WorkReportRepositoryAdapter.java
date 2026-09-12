package com.pharm.pharmavigil_platform.repository.adapter;

import com.pharm.pharmavigil_platform.domain.WorkReport;
import com.pharm.pharmavigil_platform.exception.ResourceNotFoundException;
import com.pharm.pharmavigil_platform.mapper.UserMapper;
import com.pharm.pharmavigil_platform.mapper.WorkReportMapper;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import com.pharm.pharmavigil_platform.repository.WorkReportRepository;
import com.pharm.pharmavigil_platform.repository.entities.WorkReportEntity;
import com.pharm.pharmavigil_platform.repository.jpa.JpaDepartmentRepository;
import com.pharm.pharmavigil_platform.repository.jpa.JpaMachineRepository;
import com.pharm.pharmavigil_platform.repository.jpa.JpaProductRepository;
import com.pharm.pharmavigil_platform.repository.jpa.JpaShiftRepository;
import com.pharm.pharmavigil_platform.repository.jpa.JpaStopTypeRepository;
import com.pharm.pharmavigil_platform.repository.jpa.JpaWorkReportRepository;
import com.pharm.pharmavigil_platform.repository.listing.WorkReportListing;
import com.pharm.pharmavigil_platform.repository.specs.SupervisorWorkReportFilterSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WorkReportRepositoryAdapter implements WorkReportRepository, WorkReportListing {

    private final JpaWorkReportRepository jpaWorkReportRepository;
    private final JpaShiftRepository jpaShiftRepository;
    private final JpaMachineRepository jpaMachineRepository;
    private final JpaStopTypeRepository jpaStopTypeRepository;
    private final JpaDepartmentRepository jpaDepartmentRepository;
    private final JpaProductRepository jpaProductRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final WorkReportMapper workReportMapper;

    @Override
    public boolean existsById(UUID id) {
        return jpaWorkReportRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        jpaWorkReportRepository.deleteById(id);
    }

    @Override
    public WorkReport save(WorkReport workReport) {
        WorkReportEntity entity = workReportMapper.toEntity(workReport);
        entity.setStaffUser(userRepository.findById(workReport.getStaffUser().getId())
                .map(userMapper::toEntity)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + workReport.getStaffUser().getId())));
        entity.setShift(jpaShiftRepository.getReferenceById(workReport.getShiftId()));
        for (int i = 0; i < entity.getMachines().size(); i++) {
            var machineEntity = entity.getMachines().get(i);
            var machineDomain = workReport.getMachines().get(i);
            machineEntity.setMachine(jpaMachineRepository.getReferenceById(machineDomain.getMachineId()));
            if (machineDomain.getDepartmentId() != null) {
                machineEntity.setDepartment(jpaDepartmentRepository.getReferenceById(machineDomain.getDepartmentId()));
            }
            for (int j = 0; j < machineEntity.getProducts().size(); j++) {
                var productEntity = machineEntity.getProducts().get(j);
                var productDomain = machineDomain.getProducts().get(j);
                if (productDomain.getProductId() != null) {
                    productEntity.setProduct(jpaProductRepository.getReferenceById(productDomain.getProductId()));
                }
                for (int k = 0; k < productEntity.getStops().size(); k++) {
                    productEntity.getStops().get(k).setStopType(
                            jpaStopTypeRepository.getReferenceById(productDomain.getStops().get(k).getStopTypeId()));
                }
            }
        }
        return workReportMapper.toDomain(jpaWorkReportRepository.save(entity));
    }

    @Override
    public boolean existsByStaffUserIdAndShiftIdAndReportDate(UUID staffUserId, UUID shiftId, LocalDate reportDate) {
        return jpaWorkReportRepository.existsByStaffUserIdAndShiftIdAndReportDate(staffUserId, shiftId, reportDate);
    }

    public Optional<WorkReportEntity> findById(UUID id) {
        return jpaWorkReportRepository.findById(id);
    }

    @Override
    public Page<WorkReportEntity> findAll(SupervisorWorkReportFilterSpec spec, Pageable pageable) {
        return jpaWorkReportRepository.findAll(spec, pageable);
    }
}
