package com.pharm.pharmavigil_platform.repository.entities;

import com.pharm.pharmavigil_platform.repository.converters.StringListConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "departments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "text", nullable = false)
    private List<String> stages;

    @Column(name = "has_outputs", nullable = false)
    private boolean hasOutputs;

    @ElementCollection
    @CollectionTable(name = "department_units", joinColumns = @JoinColumn(name = "department_id"))
    @Column(name = "unit", length = 100)
    @Builder.Default
    private List<String> units = new ArrayList<>();

    @Column(name = "show_consignee", nullable = false)
    private boolean showConsignee;

    @Column(name = "is_terminal_department", nullable = false)
    private boolean terminalDepartment;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MachineEntity> machines = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "department_supervisors",
            joinColumns = @JoinColumn(name = "department_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<UserEntity> supervisors = new ArrayList<>();

    @Column(name = "standard_holding_time", nullable = false)
    private int standardHoldingTime;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DepartmentExceptionalHoldingEntity> exceptionalHoldings = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by", length = 255)
    private String createdBy;

    @Column(name = "last_updated_at")
    private Instant lastUpdatedAt;

    @Column(name = "last_updated_by", length = 255)
    private String lastUpdatedBy;
}
