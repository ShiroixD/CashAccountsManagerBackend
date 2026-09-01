package org.dev.cash_accounts_manager_backend.repositories;

import org.dev.cash_accounts_manager_backend.enums.RoleEnum;
import org.dev.cash_accounts_manager_backend.models.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest(
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                RoleRepository.class
        }),
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                LogRepository.class,
                UserRepository.class,
                PersonalInfoRepository.class,
                AddressRepository.class,
                BankAccountRepository.class,
                ActionRecordRepository.class
        })
)
@PropertySource("classpath:tests.properties")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    private Role superAdminRole;
    private Role adminRole;
    private Role ordinaryUserRole;

    @BeforeEach
    void setUp() {
        superAdminRole = roleRepository.save( new Role(RoleEnum.SUPER_ADMIN, "Super admin user"));
        adminRole = roleRepository.save( new Role(RoleEnum.ADMIN, "Admin user"));
        ordinaryUserRole = roleRepository.save(new Role(RoleEnum.USER, "Ordinary user"));
    }

    @AfterEach
    void tearDown() {
        roleRepository.delete(superAdminRole);
        roleRepository.delete(adminRole);
        roleRepository.delete(ordinaryUserRole);

        superAdminRole = null;
        adminRole = null;
        ordinaryUserRole = null;
    }

    @Test
    void givenRoles_whenFindByCode_thenGetRolesByCode() {
        Optional<Role> foundSuperAdminRole = roleRepository.findByCode(RoleEnum.SUPER_ADMIN);
        Optional<Role> foundAdminRole = roleRepository.findByCode(RoleEnum.ADMIN);
        Optional<Role> foundUserRole = roleRepository.findByCode(RoleEnum.USER);

        assertTrue(foundSuperAdminRole.isPresent() && superAdminRole.equals(foundSuperAdminRole.get()), "Should find super admin role");
        assertTrue(foundAdminRole.isPresent() && adminRole.equals(foundAdminRole.get()), "Should find admin role");
        assertTrue(foundUserRole.isPresent() && ordinaryUserRole.equals(foundUserRole.get()), "Should find user role");
    }
}
