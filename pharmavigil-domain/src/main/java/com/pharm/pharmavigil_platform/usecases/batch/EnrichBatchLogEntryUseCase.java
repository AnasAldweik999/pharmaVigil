package com.pharm.pharmavigil_platform.usecases.batch;

import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.domain.Machine;
import com.pharm.pharmavigil_platform.domain.Product;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.repository.ProductRepository;

public class EnrichBatchLogEntryUseCase {

    private final DepartmentRepository departmentRepository;
    private final MachineRepository machineRepository;
    private final ProductRepository productRepository;

    public EnrichBatchLogEntryUseCase(DepartmentRepository departmentRepository,
                                       MachineRepository machineRepository,
                                       ProductRepository productRepository) {
        this.departmentRepository = departmentRepository;
        this.machineRepository = machineRepository;
        this.productRepository = productRepository;
    }

    public void execute(BatchLogEntry entry) {
        // A completed entry inherits no department/machine (it's not a physical movement), so
        // there's nothing to enrich for those two fields in that case.
        if (entry.getDepartmentId() != null) {
            Department department = departmentRepository.findById(entry.getDepartmentId()).orElseThrow();
            entry.setDepartmentName(department.getName());
            entry.setTerminalDepartment(department.isTerminalDepartment());
        }

        if (entry.getMachineId() != null) {
            Machine machine = machineRepository.findById(entry.getMachineId()).orElseThrow();
            entry.setMachineName(machine.getName());
        }

        Product product = productRepository.findById(entry.getProductId()).orElseThrow();
        entry.setProductName(product.getName());
    }
}
