package org.dev.cash_accounts_manager_backend.repositories;

import org.dev.cash_accounts_manager_backend.enums.RoleEnum;
import org.dev.cash_accounts_manager_backend.models.Role;
import org.dev.cash_accounts_manager_backend.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest(
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
            RoleRepository.class,
            UserRepository.class,
            BCryptPasswordEncoder.class
        }),
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
            LogRepository.class,
            PersonalInfoRepository.class,
            AddressRepository.class,
            BankAccountRepository.class,
            ActionRecordRepository.class
        })
)
@PropertySource("classpath:tests.properties")
class UserRepositoryTest {

    @TestConfiguration
    static class UserRepositoryTestContextConfiguration {
        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    private Role superAdminRole;
    private Role adminRole;
    private Role ordinaryUserRole;

    private User superAdminUser;
    private User adminUser;
    private User ordinaryUser1;
    private User ordinaryUser2;

    @BeforeEach
    void setUp() {
        superAdminRole = roleRepository.save( new Role(RoleEnum.SUPER_ADMIN, "Super admin user"));
        adminRole = roleRepository.save( new Role(RoleEnum.ADMIN, "Admin user"));
        ordinaryUserRole = roleRepository.save(new Role(RoleEnum.USER, "Ordinary user"));

        superAdminUser =  userRepository.save(new User("Conny Jose", "ConJo", passwordEncoder.encode("gr3@a21"), superAdminRole));
        adminUser =  userRepository.save(new User("Bob Tobby", "Bobson", passwordEncoder.encode("bo2a@231"), adminRole));
        ordinaryUser1 =  userRepository.save(new User("John Sonny", "Johny Son", passwordEncoder.encode("123fea4"), ordinaryUserRole));
        ordinaryUser2 =  userRepository.save(new User("Emma Thomson", "Emily", passwordEncoder.encode("536asf"), ordinaryUserRole));
    }

    @AfterEach
    void tearDown() {
        userRepository.delete(superAdminUser);
        userRepository.delete(adminUser);
        userRepository.delete(ordinaryUser1);
        userRepository.delete(ordinaryUser2);

        roleRepository.delete(superAdminRole);
        roleRepository.delete(adminRole);
        roleRepository.delete(ordinaryUserRole);

        superAdminUser = null;
        adminUser = null;
        ordinaryUser1 = null;
        ordinaryUser2 = null;

        superAdminRole = null;
        adminRole = null;
        ordinaryUserRole = null;
    }

    @Test
    void givenUsers_whenSecondDisabled_thenGetTwoActiveUsers() {
        ordinaryUser2.setDisabled(true);

        List<User> foundUsers = userRepository.findAllActive();

        assertFalse(foundUsers.isEmpty(), "Should not be empty");
        assertEquals(2, foundUsers.size(), "Should contain two users");
        assertFalse(foundUsers.contains(superAdminUser), "Super admin should be omitted");
        assertTrue(foundUsers.contains(adminUser), "Should contain admin user");
        assertTrue(foundUsers.contains(ordinaryUser1), "Should contain ordinary user 1");
        assertFalse(foundUsers.contains(ordinaryUser2), "Should not contain ordinary user 2");
    }

    @Test
    void givenUsers_whenSecondDisabled_thenGetPageableWithTwoActiveUsers() {
        ordinaryUser2.setDisabled(true);

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> foundUsersPage = userRepository.findAllActive(pageable);
        List<User> foundUsers = foundUsersPage.stream().toList();

        assertEquals(1, foundUsersPage.getTotalPages(), "Should contain one page");
        assertEquals(2, foundUsersPage.getTotalElements(), "Should contain two users");
        assertFalse(foundUsers.contains(superAdminUser), "Super admin should be omitted");
        assertTrue(foundUsers.contains(adminUser), "Should contain admin user");
        assertTrue(foundUsers.contains(ordinaryUser1), "Should contain ordinary user 1");
        assertFalse(foundUsers.contains(ordinaryUser2), "Should not contain ordinary user 2");
    }

    @Test
    void givenUsers_whenFindByUsername_thenGetUserWithUsername() {
        Optional<User> foundUserOptional = userRepository.findByUsername(ordinaryUser1.getUsername());

        assertTrue(foundUserOptional.isPresent(), "User with username " + ordinaryUser1.getUsername() + " should exist");
    }
}