package com.pharm.pharmavigil_platform.usecases.workreport;

import com.pharm.pharmavigil_platform.domain.WorkReport;
import com.pharm.pharmavigil_platform.domain.WorkReportMachine;
import com.pharm.pharmavigil_platform.domain.WorkReportProduct;
import com.pharm.pharmavigil_platform.domain.WorkReportProductStop;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.repository.ProductRepository;
import com.pharm.pharmavigil_platform.repository.ShiftRepository;
import com.pharm.pharmavigil_platform.repository.StopTypeRepository;

/**
 * Resolves and populates the denormalized name fields (shiftName, machineName, departmentName,
 * productName, stopTypeName) on an already-validated work report, from the IDs the client
 * submitted. Kept separate from the validators — a validator's job is to check and report
 * violations, not mutate domain state as a side effect. Run only after validation succeeds, so we
 * never resolve names for a report that's about to be rejected.
 */
public class EnrichWorkReportUseCase {

    private final ShiftRepository shiftRepository;
    private final MachineRepository machineRepository;
    private final DepartmentRepository departmentRepository;
    private final ProductRepository productRepository;
    private final StopTypeRepository stopTypeRepository;

    public EnrichWorkReportUseCase(ShiftRepository shiftRepository,
                                    MachineRepository machineRepository,
                                    DepartmentRepository departmentRepository,
                                    ProductRepository productRepository,
                                    StopTypeRepository stopTypeRepository) {
        this.shiftRepository = shiftRepository;
        this.machineRepository = machineRepository;
        this.departmentRepository = departmentRepository;
        this.productRepository = productRepository;
        this.stopTypeRepository = stopTypeRepository;
    }

    public void execute(WorkReport report) {
        if (report.getShiftId() != null) {
            shiftRepository.findById(report.getShiftId()).ifPresent(s -> report.setShiftName(s.getName()));
        }

        if (report.getMachines() == null) return;
        for (WorkReportMachine machine : report.getMachines()) {
            if (machine.getMachineId() != null) {
                machineRepository.findById(machine.getMachineId()).ifPresent(m -> machine.setMachineName(m.getName()));
            }

            if (machine.getDepartmentId() != null) {
                departmentRepository.findById(machine.getDepartmentId()).ifPresent(d -> machine.setDepartmentName(d.getName()));
            }

            if (machine.getProducts() == null) continue;
            for (WorkReportProduct product : machine.getProducts()) {
                if (product.getProductId() != null) {
                    productRepository.findById(product.getProductId()).ifPresent(p -> product.setProductName(p.getName()));
                }

                if (product.getStops() == null) continue;
                for (WorkReportProductStop stop : product.getStops()) {
                    if (stop.getStopTypeId() != null) {
                        stopTypeRepository.findById(stop.getStopTypeId()).ifPresent(st -> stop.setStopTypeName(st.getName()));
                    }
                }
            }
        }
    }
}
