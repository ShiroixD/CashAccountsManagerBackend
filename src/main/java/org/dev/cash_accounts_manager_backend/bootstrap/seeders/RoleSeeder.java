package org.dev.cash_accounts_manager_backend.bootstrap.seeders;

import org.dev.cash_accounts_manager_backend.enums.RoleEnum;
import org.dev.cash_accounts_manager_backend.models.Role;
import org.dev.cash_accounts_manager_backend.repositories.RoleRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Class loading roles from external local file<br>
 * It is run on application start
 *
 * @author Fabian Frontczak
 */
@Component
@Order(2)
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;

    /**
     * Class constructor with DI (dependency injection) for roles repository
     * @param roleRepository repository containing roles
     */
    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
    }

    /**
     *  Method getting all roles from external local file and saving them to database
     */
    private void loadRoles() {
        RoleEnum[] roleNames = new RoleEnum[] { RoleEnum.SUPER_ADMIN, RoleEnum.ADMIN, RoleEnum.USER };
        Map<RoleEnum, String> roleDescriptionMap = Map.of(
                RoleEnum.SUPER_ADMIN, "Super Administrator role",
                RoleEnum.ADMIN, "Administrator role",
                RoleEnum.USER, "Default user role"
        );

        Arrays.stream(roleNames).forEach((roleName) -> {
            Optional<Role> optionalRole = roleRepository.findByCode(roleName);

            optionalRole.ifPresentOrElse(System.out::println, () -> {
                Role roleToCreate = new Role();

                roleToCreate.setCode(roleName);
                roleToCreate.setDescription(roleDescriptionMap.get(roleName));

                roleRepository.save(roleToCreate);
            });
        });
    }
}
