package com.pharm.pharmavigil_platform.init;

import com.pharm.pharmavigil_platform.domain.AccountType;
import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.domain.UserStatus;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-admin.email}")
    private String supervisorEmail;

    @Value("${app.default-admin.password}")
    private String supervisorPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByEmailIgnoreCase(supervisorEmail)) {
            userRepository.save(User.builder()
                    .name("System Supervisor")
                    .email(supervisorEmail)
                    .passwordHash(passwordEncoder.encode(supervisorPassword))
                    .accountType(AccountType.SUPERVISOR)
                    .lastUpdatedAt(Instant.now())
                    .lastUpdatedBy("Default")
                    .createdAt(Instant.now())
                    .createdBy("DEFAULT")
                    .roles(Set.of(
                            UserRole.DASHBOARD_VIEWER,
                            UserRole.MACHINE_MANAGER,
                            UserRole.STOP_TYPES_MANAGER,
                            UserRole.USERS_MANAGER,
                            UserRole.SHIFT_MANAGER
                    ))
                    .status(UserStatus.ACTIVE)
                    .build());
            log.info("Default supervisor created: {}", supervisorEmail);
        }
    }
}
