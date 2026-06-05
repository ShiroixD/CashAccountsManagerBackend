package org.dev.cash_accounts_manager_backend.bootstrap.seeders;

import org.dev.cash_accounts_manager_backend.dtos.RegisterUserDto;
import org.dev.cash_accounts_manager_backend.enums.RoleEnum;
import org.dev.cash_accounts_manager_backend.models.Role;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.repositories.RoleRepository;
import org.dev.cash_accounts_manager_backend.repositories.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Class creating super admin account in case it's not present<br>
 * It is run on application start
 *
 * @author Fabian Frontczak
 */
@Component
@Order(3)
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * Class constructor with DI (dependency injection) for roles, users repositories and password encoder
     * @param roleRepository repository containing roles
     * @param userRepository repository containing users
     * @param passwordEncoder password encoder
     */
    public AdminSeeder(
            RoleRepository roleRepository,
            UserRepository  userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createSuperAdministrator();
    }

    /**
     *  Method creating super admin account with default password it's not present
     */
    private void createSuperAdministrator() {
        RegisterUserDto userDto = new RegisterUserDto("super.admin", "123456", "Super Admin");

        Optional<Role> optionalRole = roleRepository.findByCode(RoleEnum.SUPER_ADMIN);
        Optional<User> optionalUser = userRepository.findByUsername(userDto.username());

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }

        var user = new User(userDto.fullName(), userDto.username(), passwordEncoder.encode(userDto.password()), optionalRole.get());

        userRepository.save(user);
    }
}
