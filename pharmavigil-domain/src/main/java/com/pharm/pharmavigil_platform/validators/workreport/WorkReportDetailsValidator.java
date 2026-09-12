package com.pharm.pharmavigil_platform.validators.workreport;

import com.pharm.pharmavigil_platform.domain.*;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.repository.ProductRepository;
import com.pharm.pharmavigil_platform.repository.StopTypeRepository;
import com.pharm.pharmavigil_platform.util.DurationFormatter;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.*;
import java.util.regex.Pattern;

public class WorkReportDetailsValidator implements Validator<WorkReport> {

    private static final long MAX_OUTPUT = 999_999_999_999_999L;
    private static final long MAX_STOP_DURATION_MINUTES = 9_999_999_999L;
    private static final int MAX_NOTES_LENGTH = 255;
    private static final int MAX_CONSIGNEE_LENGTH = 255;

    private final MachineRepository machineRepository;
    private final StopTypeRepository stopTypeRepository;
    private final DepartmentRepository departmentRepository;
    private final ProductRepository productRepository;
    private final DurationFormatter durationFormatter;
    private final Pattern batchNoPattern;

    public WorkReportDetailsValidator(MachineRepository machineRepository,
                                       StopTypeRepository stopTypeRepository,
                                       DepartmentRepository departmentRepository,
                                       ProductRepository productRepository,
                                       DurationFormatter durationFormatter,
                                       String batchNoRegex) {
        this.machineRepository = machineRepository;
        this.stopTypeRepository = stopTypeRepository;
        this.departmentRepository = departmentRepository;
        this.productRepository = productRepository;
        this.durationFormatter = durationFormatter;
        this.batchNoPattern = Pattern.compile(batchNoRegex);
    }

    @Override
    public Set<SystemViolation> validate(WorkReport report) {
        Set<SystemViolation> violations = new LinkedHashSet<>();

        if (report.getMachines() == null || report.getMachines().isEmpty()) {
            violations.add(new SystemViolation("machines", "machines.required"));
            return violations;
        }

        if (hasDuplicateMachines(report.getMachines())) {
            violations.add(new SystemViolation("machines", "machines.duplicate"));
        }

        report.getMachines().forEach(machine -> validateMachine(machine, violations));

        return violations;
    }

    private boolean hasDuplicateMachines(List<WorkReportMachine> machines) {
        List<UUID> machineIds = machines.stream().map(WorkReportMachine::getMachineId).toList();
        return machineIds.size() != new HashSet<>(machineIds).size();
    }

    private void validateMachine(WorkReportMachine machine, Set<SystemViolation> violations) {
        validateMachineId(machine, violations);
        Department department = resolveDepartment(machine, violations);

        if (machine.getStatus() == null) {
            violations.add(new SystemViolation("machines.machine.status", "machines.machine.status.required"));
        }

        if (machine.getProducts() == null || machine.getProducts().isEmpty()) {
            violations.add(new SystemViolation("machines.machine.products", "machines.machine.products.required"));
            return;
        }

        for (WorkReportProduct product : machine.getProducts()) {
            validateProduct(product, department, violations);
        }
    }

    private void validateMachineId(WorkReportMachine machine, Set<SystemViolation> violations) {
        if (machine.getMachineId() == null) {
            violations.add(new SystemViolation("machines.machine.id", "machines.machine.id.required"));
        } else if (!machineRepository.existsByIdAndActiveTrue(machine.getMachineId())) {
            violations.add(new SystemViolation("machines.machine.id", "machines.machine.not.found"));
        }
    }

    private Department resolveDepartment(WorkReportMachine machine, Set<SystemViolation> violations) {
        UUID departmentId = machine.getDepartmentId();
        if (departmentId == null) {
            violations.add(new SystemViolation("machines.machine.department.id", "machines.machine.department.id.required"));
            return null;
        }

        Optional<Department> department = departmentRepository.findById(departmentId);
        if (department.isEmpty()) {
            violations.add(new SystemViolation("machines.machine.department.id", "machines.machine.department.not.found"));
            return null;
        }
        if (!department.get().isActive()) {
            violations.add(new SystemViolation("machines.machine.department.id", "machines.machine.department.inactive"));
            return null;
        }

        UUID machineId = machine.getMachineId();
        if (machineId != null && !departmentRepository.existsByIdAndMachineId(departmentId, machineId)) {
            violations.add(new SystemViolation("machines.machine.department.id", "machines.machine.department.machine.mismatch"));
        }

        return department.get();
    }

    private void validateProduct(WorkReportProduct product, Department department, Set<SystemViolation> violations) {
        validateProductId(product, violations);

        if (product.getBatchNo() == null || product.getBatchNo().isBlank()) {
            violations.add(new SystemViolation("machines.machine.product.batch.no", "machines.machine.product.batch.no.required"));
        } else if (!batchNoPattern.matcher(product.getBatchNo()).matches()) {
            violations.add(new SystemViolation("machines.machine.product.batch.no", "machines.machine.product.batch.no.invalid.format"));
        }

        validateStages(product, department, violations);
        validateDepartmentRules(product, department, violations);
        validateStops(product, violations);
        validateQuality(product, violations);
    }

    private void validateProductId(WorkReportProduct product, Set<SystemViolation> violations) {
        if (product.getProductId() == null) {
            violations.add(new SystemViolation("machines.machine.product.id", "machines.machine.product.id.required"));
        } else if (!productRepository.existsByIdAndActiveTrue(product.getProductId())) {
            violations.add(new SystemViolation("machines.machine.product.id", "machines.machine.product.not.found"));
        }
    }

    private void validateStages(WorkReportProduct product, Department department, Set<SystemViolation> violations) {
        if (department == null || product.getStages() == null || product.getStages().isEmpty()) return;

        boolean hasUnknownStage = product.getStages().keySet().stream()
                .anyMatch(key -> !isKnownStage(department, key));
        if (hasUnknownStage) {
            violations.add(new SystemViolation("machines.machine.product.stages", "machines.machine.product.stages.invalid"));
        }
    }

    private boolean isKnownStage(Department department, String stageKey) {
        return department.getStages() != null
                && department.getStages().stream().anyMatch(s -> s.equalsIgnoreCase(stageKey));
    }

    private void validateDepartmentRules(WorkReportProduct product, Department department, Set<SystemViolation> violations) {
        if (department == null) return;

        if (department.isHasOutputs()) {
            validateOutputAndUnit(product, department, violations);
        }
        if (department.isShowConsignee()) {
            validateConsignee(product, violations);
        }
    }

    private void validateOutputAndUnit(WorkReportProduct product, Department department, Set<SystemViolation> violations) {
        if (product.getOutput() < 0 || product.getOutput() > MAX_OUTPUT) {
            violations.add(new SystemViolation("machines.machine.product.output", "machines.machine.product.output.invalid"));
        }
        if (product.getOutput() <= 0) {
            violations.add(new SystemViolation("machines.machine.product.output", "machines.machine.product.output.required"));
        }

        if (product.getUnit() == null || product.getUnit().isBlank()) {
            violations.add(new SystemViolation("machines.machine.product.unit", "machines.machine.product.unit.required"));
            return;
        }

        boolean validUnit = department.getUnits() != null
                && department.getUnits().stream().anyMatch(u -> u.equalsIgnoreCase(product.getUnit()));
        if (!validUnit) {
            violations.add(new SystemViolation("machines.machine.product.unit", "machines.machine.product.unit.invalid"));
        }
    }

    private void validateConsignee(WorkReportProduct product, Set<SystemViolation> violations) {
        if (product.getConsignee() != null && product.getConsignee().length() > MAX_CONSIGNEE_LENGTH) {
            violations.add(new SystemViolation("machines.machine.product.consignee", "machines.machine.product.consignee.too.long"));
        }
    }

    private void validateStops(WorkReportProduct product, Set<SystemViolation> violations) {
        if (product.getStops() == null || product.getStops().isEmpty()) return;

        product.getStops().forEach(stop -> validateStop(stop, violations));
    }

    private void validateStop(WorkReportProductStop stop, Set<SystemViolation> violations) {
        validateStopType(stop, violations);
        validateStopDuration(stop, violations);
        validateStopNote(stop, violations);
    }

    private void validateStopNote(WorkReportProductStop stop, Set<SystemViolation> violations) {
        if (stop.getNote() != null && stop.getNote().length() > MAX_NOTES_LENGTH) {
            violations.add(new SystemViolation("machines.machine.product.stop.note", "machines.machine.product.stop.note.too.long"));
        }
    }

    private void validateStopType(WorkReportProductStop stop, Set<SystemViolation> violations) {
        if (stop.getStopTypeId() == null) {
            violations.add(new SystemViolation("machines.machine.product.stop.type.id", "machines.machine.product.stop.type.id.required"));
            return;
        }

        Optional<StopType> stopType = stopTypeRepository.findById(stop.getStopTypeId());
        if (stopType.isEmpty()) {
            violations.add(new SystemViolation("machines.machine.product.stop.type.id", "machines.machine.product.stop.type.not.found"));
        } else if (!stopType.get().isActive()) {
            violations.add(new SystemViolation("machines.machine.product.stop.type.id", "machines.machine.product.stop.type.inactive"));
        }
    }

    private void validateStopDuration(WorkReportProductStop stop, Set<SystemViolation> violations) {
        if (!durationFormatter.isValidFormat(stop.getDuration())) {
            violations.add(new SystemViolation("machines.machine.product.stop.duration", "machines.machine.product.stop.duration.invalid.format"));
            return;
        }

        long minutes = durationFormatter.toMinutes(stop.getDuration());
        if (minutes < 1 || minutes > MAX_STOP_DURATION_MINUTES) {
            violations.add(new SystemViolation("machines.machine.product.stop.duration", "machines.machine.product.stop.duration.invalid"));
        }
    }

    private void validateQuality(WorkReportProduct product, Set<SystemViolation> violations) {
        if (product.isDeviation() && (product.getDeviationDetails() == null || product.getDeviationDetails().isBlank())) {
            violations.add(new SystemViolation("machines.machine.product.deviation.details", "machines.machine.product.deviation.details.required"));
        }
        if (product.isHold() && (product.getHoldDetails() == null || product.getHoldDetails().isBlank())) {
            violations.add(new SystemViolation("machines.machine.product.hold.details", "machines.machine.product.hold.details.required"));
        }
    }
}
