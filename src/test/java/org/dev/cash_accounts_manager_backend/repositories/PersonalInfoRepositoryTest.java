package org.dev.cash_accounts_manager_backend.repositories;

import org.dev.cash_accounts_manager_backend.enums.RoleEnum;
import org.dev.cash_accounts_manager_backend.models.Role;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.models.person.Address;
import org.dev.cash_accounts_manager_backend.models.person.PersonalInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest(
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                RoleRepository.class,
                UserRepository.class,
                PersonalInfoRepository.class,
                BCryptPasswordEncoder.class
        }),
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                LogRepository.class,
                AddressRepository.class,
                BankAccountRepository.class,
                ActionRecordRepository.class
        })
)
@PropertySource("classpath:tests.properties")
class PersonalInfoRepositoryTest {

    @TestConfiguration
    static class PersonalInfoRepositoryTestContextConfiguration {
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

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PersonalInfoRepository personalInfoRepository;

    private Role ordinaryUserRole;
    private User ordinaryUser1;
    private User ordinaryUser2;
    private Address address1;
    private Address address2;
    private PersonalInfo personalInfo1;
    private PersonalInfo personalInfo2;

    @BeforeEach
    void setUp() {
        ordinaryUserRole = roleRepository.save(new Role(RoleEnum.USER, "Ordinary user"));
        ordinaryUser1 =  userRepository.save(new User("John Sonny", "Johny Son", passwordEncoder.encode("123fea4"), ordinaryUserRole));
        ordinaryUser2 =  userRepository.save(new User("Emma Thomson", "Emily", passwordEncoder.encode("536asf"), ordinaryUserRole));
        address1 = addressRepository.save(new Address("Sunny St.", "12c", "45L", "Nova city", "Green state", "98-19", "Poland"));
        address2 = addressRepository.save(new Address("Rainy St.", "42", "10", "Meteor", "Explosion state", "18-134", "Germany"));
        personalInfo1 = personalInfoRepository.save(new PersonalInfo(ordinaryUser1, "Jonathan", "Sonny", "jon.son@gmail.com", "+48538231330", address1, "12340991342"));
        personalInfo2 = personalInfoRepository.save(new PersonalInfo(ordinaryUser2, "Eve", "Dires", "eve.dires@gmail.com", "+48506121850", address1, "53689543325"));
    }

    @AfterEach
    void tearDown() {
        personalInfoRepository.delete(personalInfo1);
        personalInfoRepository.delete(personalInfo2);
        addressRepository.delete(address1);
        addressRepository.delete(address2);
        userRepository.delete(ordinaryUser1);
        userRepository.delete(ordinaryUser2);
        roleRepository.delete(ordinaryUserRole);

        personalInfo1 = null;
        personalInfo2 = null;
        address1 = null;
        address2 = null;
        ordinaryUser1 = null;
        ordinaryUser2 = null;
        ordinaryUserRole = null;
    }

    @Test
    void givenUsersWithPersonalInfo_whenFindByOwner_thenGetOwnerPersonalInfo() {
        Optional<PersonalInfo> foundPersonalInfoUser1 = personalInfoRepository.findByOwner(ordinaryUser1.getId());
        Optional<PersonalInfo> foundPersonalInfoUser2 = personalInfoRepository.findByOwner(ordinaryUser2.getId());

        assertTrue(foundPersonalInfoUser1.isPresent(), "User 1 should have personal info");
        assertTrue(foundPersonalInfoUser2.isPresent(), "User 2 should have personal info");
    }

    @Test
    void givenUsersWithPersonalInfo_whenCountByUserId_thenGetPersonalInfoCount() {
        int personalInfoCountUser1 = personalInfoRepository.countByUserId(ordinaryUser1.getId());
        int personalInfoCountUser2 = personalInfoRepository.countByUserId(ordinaryUser2.getId());

        assertEquals(1, personalInfoCountUser1, "User 1 should have only one personal info occurrence");
        assertEquals(1, personalInfoCountUser2, "User 2 should have only one personal info occurrence");
    }

    @Test
    void givenUsersWithPersonalInfo_whenExistsByPersonalCode_thenCheckResults() {
        boolean personalInfoCheck1 = personalInfoRepository.existsByPersonalCode("12340991342");
        boolean personalInfoCheck2 = personalInfoRepository.existsByPersonalCode("43289143329");

        assertTrue(personalInfoCheck1, "Personal info 1 should be found");
        assertFalse(personalInfoCheck2, "Personal info 2 should not be found");
    }
}
