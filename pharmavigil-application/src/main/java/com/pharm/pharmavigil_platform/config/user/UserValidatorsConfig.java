package com.pharm.pharmavigil_platform.config.user;

import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import com.pharm.pharmavigil_platform.service.RolesProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import com.pharm.pharmavigil_platform.validators.user.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UserValidatorsConfig {

    @Bean
    public UserExistsValidator userExistsValidator(UserRepository userRepository) {
        return new UserExistsValidator(userRepository);
    }

    @Bean
    public UserEmailUniqueValidator userEmailUniqueValidator(UserRepository userRepository) {
        return new UserEmailUniqueValidator(userRepository);
    }

    @Bean
    public UserEmailNotTakenByOthersValidator userEmailNotTakenByOthersValidator(UserRepository userRepository) {
        return new UserEmailNotTakenByOthersValidator(userRepository);
    }

    @Bean
    public UserNotDefaultAdminValidator userNotDefaultAdminValidator(
            @Value("${app.default-admin.email}") String defaultAdminEmail) {
        return new UserNotDefaultAdminValidator(defaultAdminEmail);
    }

    @Bean
    public UserStatusNotPendingValidator userStatusNotPendingValidator() {
        return new UserStatusNotPendingValidator();
    }

    @Bean
    public UserFullNameValidator userFullNameValidator() {
        return new UserFullNameValidator();
    }

    @Bean
    public UserEmailFormatValidator userEmailFormatValidator() {
        return new UserEmailFormatValidator();
    }

    @Bean
    public UserRolesValidator userRolesValidator(RolesProvider rolesProvider) {
        return new UserRolesValidator(rolesProvider);
    }

    @Bean
    public UserUsernameValidator userUsernameValidator() {
        return new UserUsernameValidator();
    }

    @Bean
    public UserUsernameUniqueValidator userUsernameUniqueValidator(UserRepository userRepository) {
        return new UserUsernameUniqueValidator(userRepository);
    }

    @Bean
    public UserUsernameNotTakenByOthersValidator userUsernameNotTakenByOthersValidator(UserRepository userRepository) {
        return new UserUsernameNotTakenByOthersValidator(userRepository);
    }

    @Bean("userCreateValidatorChain")
    public ValidatorChain<User> userCreateValidatorChain(
            UserFullNameValidator userFullNameValidator,
            UserEmailFormatValidator userEmailFormatValidator,
            UserEmailUniqueValidator userEmailUniqueValidator,
            UserRolesValidator userRolesValidator,
            UserUsernameValidator userUsernameValidator,
            UserUsernameUniqueValidator userUsernameUniqueValidator) {
        return new ValidatorChain<>(List.of(userFullNameValidator, userEmailFormatValidator, userEmailUniqueValidator, userRolesValidator, userUsernameValidator, userUsernameUniqueValidator));
    }

    @Bean("userUpdateValidatorChain")
    public ValidatorChain<User> userUpdateValidatorChain(
            UserFullNameValidator userFullNameValidator,
            UserEmailFormatValidator userEmailFormatValidator,
            UserExistsValidator userExistsValidator,
            UserEmailNotTakenByOthersValidator userEmailNotTakenByOthersValidator,
            UserRolesValidator userRolesValidator,
            UserUsernameValidator userUsernameValidator,
            UserUsernameNotTakenByOthersValidator userUsernameNotTakenByOthersValidator) {
        return new ValidatorChain<>(List.of(userFullNameValidator, userEmailFormatValidator, userExistsValidator, userEmailNotTakenByOthersValidator, userRolesValidator, userUsernameValidator, userUsernameNotTakenByOthersValidator));
    }

    @Bean
    public SupervisorAttachedToDepartmentValidator supervisorAttachedToDepartmentValidator(DepartmentRepository departmentRepository) {
        return new SupervisorAttachedToDepartmentValidator(departmentRepository);
    }

    @Bean("userToggleValidatorChain")
    public ValidatorChain<User> userToggleValidatorChain(
            UserExistsValidator userExistsValidator,
            UserNotDefaultAdminValidator userNotDefaultAdminValidator,
            UserStatusNotPendingValidator userStatusNotPendingValidator,
            SupervisorAttachedToDepartmentValidator supervisorAttachedToDepartmentValidator) {
        return new ValidatorChain<>(List.of(userExistsValidator, userNotDefaultAdminValidator, userStatusNotPendingValidator, supervisorAttachedToDepartmentValidator));
    }
}
