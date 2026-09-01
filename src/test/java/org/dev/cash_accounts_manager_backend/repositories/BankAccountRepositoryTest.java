package org.dev.cash_accounts_manager_backend.repositories;

import org.dev.cash_accounts_manager_backend.enums.BankType;
import org.dev.cash_accounts_manager_backend.enums.RoleEnum;
import org.dev.cash_accounts_manager_backend.models.Role;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.models.account.BankAccount;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest(
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                RoleRepository.class,
                UserRepository.class,
                BankAccountRepository.class,
                ActionRecordRepository.class,
                BCryptPasswordEncoder.class
        }),
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                LogRepository.class,
                PersonalInfoRepository.class,
                AddressRepository.class,
        })
)
@PropertySource("classpath:tests.properties")
class BankAccountRepositoryTest {

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
    private BankAccountRepository bankAccountRepository;

    private Role ordinaryUserRole;
    private User ordinaryUser1;
    private User ordinaryUser2;
    private BankAccount bankAccount1;
    private BankAccount bankAccount2;

    @BeforeEach
    void setUp() {
        ordinaryUserRole = roleRepository.save(new Role(RoleEnum.USER, "Ordinary user"));
        ordinaryUser1 =  userRepository.save(new User("John Sonny", "Johny Son", passwordEncoder.encode("123fea4"), ordinaryUserRole));
        ordinaryUser2 =  userRepository.save(new User("Emma Thomson", "Emily", passwordEncoder.encode("536asf"), ordinaryUserRole));
        bankAccount1 = bankAccountRepository.save(new BankAccount(ordinaryUser1, "Mark personal", BankType.COMMON_SENSE_INVESTMENT, "91309060140000000212198129", new BigDecimal("245002.31")));
        bankAccount2 = bankAccountRepository.save(new BankAccount(ordinaryUser2, "Camilla savings", BankType.BRIGHT_TOMMORROW, "21301060140000000212198127", new BigDecimal("141002.17"), "7924868944"));
    }


    @AfterEach
    void tearDown() {
        bankAccountRepository.delete(bankAccount1);
        bankAccountRepository.delete(bankAccount2);
        userRepository.delete(ordinaryUser1);
        userRepository.delete(ordinaryUser2);
        roleRepository.delete(ordinaryUserRole);

        bankAccount1 = null;
        bankAccount2 = null;
        ordinaryUser1 = null;
        ordinaryUser2 = null;
        ordinaryUserRole = null;
    }

    @Test
    void givenBankAccounts_whenFindByOwner_thenGetBankAccount() {
        List<BankAccount> foundBankAccounts1 = bankAccountRepository.findByOwner(ordinaryUser1.getId());
        List<BankAccount> foundBankAccounts2 = bankAccountRepository.findByOwner(ordinaryUser2.getId());

        assertTrue(foundBankAccounts1.contains(bankAccount1), "User 1 should have bank account");
        assertTrue(foundBankAccounts2.contains(bankAccount2), "User 2 should have bank account");
    }

    @Test
    void givenBankAccounts_whenFindByOwnerAndAccountName_thenGetBankAccount() {
        Optional<BankAccount> foundBankAccountFailure1 = bankAccountRepository.findByOwnerAndAccountName(ordinaryUser1.getId(), "Mark p");
        Optional<BankAccount> foundBankAccount1 = bankAccountRepository.findByOwnerAndAccountName(ordinaryUser1.getId(), "Mark personal");
        Optional<BankAccount> foundBankAccount2 = bankAccountRepository.findByOwnerAndAccountName(ordinaryUser2.getId(), "Camilla savings");

        assertTrue(foundBankAccountFailure1.isEmpty(), "User 1 should not have bank account with name 'Mark p'");
        assertTrue(foundBankAccount1.isPresent(), "User 1 should have bank account with name Mark personal");
        assertTrue(foundBankAccount2.isPresent(), "User 2 should have bank account with name Camilla savings");
    }

    @Test
    void givenBankAccounts_whenExistsByAccountNumber_thenCheckResults() {
        boolean bankAccountCheck1 = bankAccountRepository.existsByAccountNumber("91309060140000000212198129");
        boolean bankAccountCheck2 = bankAccountRepository.existsByAccountNumber("45702060147008000216191128");

        assertTrue(bankAccountCheck1, "Bank account 1 should be found");
        assertFalse(bankAccountCheck2, "Bank account 2 should not be found");
    }
}
