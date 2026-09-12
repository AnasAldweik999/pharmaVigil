package com.pharm.pharmavigil_platform.controller.supervisor;

import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.repository.specs.StaffUserSearchSpec;
import com.pharm.pharmavigil_platform.repository.specs.SupervisorUserSearchSpec;
import com.pharm.pharmavigil_platform.repository.specs.UserSpec;
import com.pharm.pharmavigil_platform.resources.role.RoleResponse;
import com.pharm.pharmavigil_platform.resources.supervisor.StaffUserDropdownResponse;
import com.pharm.pharmavigil_platform.resources.supervisor.SupervisorUserDropdownResponse;
import com.pharm.pharmavigil_platform.resources.user.CreateUserRequest;
import com.pharm.pharmavigil_platform.resources.user.UpdateUserRequest;
import com.pharm.pharmavigil_platform.resources.user.UserFieldAvailabilityResponse;
import com.pharm.pharmavigil_platform.resources.user.UserResponse;
import com.pharm.pharmavigil_platform.security.RequiresRole;
import com.pharm.pharmavigil_platform.service.RolesProvider;
import com.pharm.pharmavigil_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/supervisor")
@RequiredArgsConstructor
public class SupervisorUserController {

    private final UserService userService;
    private final RolesProvider rolesProvider;

    @GetMapping("/users/check-email")
    @RequiresRole(UserRole.USERS_MANAGER)
    public ResponseEntity<UserFieldAvailabilityResponse> checkEmail(
            @RequestParam String email,
            @RequestParam(required = false) UUID excludeId) {
        return ResponseEntity.ok(new UserFieldAvailabilityResponse(userService.isEmailAvailable(email, excludeId)));
    }

    @GetMapping("/users/check-username")
    @RequiresRole(UserRole.USERS_MANAGER)
    public ResponseEntity<UserFieldAvailabilityResponse> checkUsername(
            @RequestParam String username,
            @RequestParam(required = false) UUID excludeId) {
        return ResponseEntity.ok(new UserFieldAvailabilityResponse(userService.isUsernameAvailable(username, excludeId)));
    }

    @GetMapping("/users/roles")
    @RequiresRole(UserRole.USERS_MANAGER)
    public ResponseEntity<List<RoleResponse>> getRoles() {
        return ResponseEntity.ok(rolesProvider.getAll());
    }

    @GetMapping("/users/{id}")
    @RequiresRole(UserRole.USERS_MANAGER)
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/users")
    @RequiresRole({UserRole.USERS_MANAGER, UserRole.DEPARTMENTS_MANAGER})
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            UserSpec spec,
            Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(spec, pageable));
    }

    @GetMapping("/staff-users")
    @RequiresRole(UserRole.DASHBOARD_VIEWER)
    public ResponseEntity<Page<StaffUserDropdownResponse>> getStaffUsers(
            StaffUserSearchSpec spec,
            Pageable pageable) {
        return ResponseEntity.ok(userService.getStaffUsers(spec, pageable));
    }

    @GetMapping("/supervisor-users")
    @RequiresRole({UserRole.DEPARTMENTS_MANAGER})
    public ResponseEntity<Page<SupervisorUserDropdownResponse>> getSupervisorUsers(
            SupervisorUserSearchSpec spec,
            Pageable pageable) {
        return ResponseEntity.ok(userService.getSupervisorUsers(spec, pageable));
    }

    @PostMapping("/users/supervisors")
    @RequiresRole(UserRole.USERS_MANAGER)
    public ResponseEntity<UserResponse> createSupervisor(@RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createSupervisor(request));
    }

    @PostMapping("/users/staff")
    @RequiresRole(UserRole.USERS_MANAGER)
    public ResponseEntity<UserResponse> createStaff(@RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createStaff(request));
    }

    @PutMapping("/users/{id}")
    @RequiresRole(UserRole.USERS_MANAGER)
    public ResponseEntity<UserResponse> update(@PathVariable UUID id,
                                               @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @PatchMapping("/users/{id}/active")
    @RequiresRole(UserRole.USERS_MANAGER)
    public ResponseEntity<UserResponse> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.toggleActive(id));
    }
}
