package org.dev.cash_accounts_manager_backend.services;

import org.dev.cash_accounts_manager_backend.dtos.*;
import org.dev.cash_accounts_manager_backend.dtos.requests.ActionRecordCreationRequest;
import org.dev.cash_accounts_manager_backend.dtos.requests.BankAccountCreationRequest;
import org.dev.cash_accounts_manager_backend.enums.BankType;
import org.dev.cash_accounts_manager_backend.enums.RoleEnum;
import org.dev.cash_accounts_manager_backend.models.Role;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.models.account.ActionRecord;
import org.dev.cash_accounts_manager_backend.models.account.BankAccount;
import org.dev.cash_accounts_manager_backend.repositories.ActionRecordRepository;
import org.dev.cash_accounts_manager_backend.repositories.BankAccountRepository;
import org.dev.cash_accounts_manager_backend.utils.Extensions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private ActionRecordRepository actionRecordRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BankAccountService bankAccountService;

    private Role ordinaryUserRole;
    private User ordinaryUser;
    private BankAccount bankAccount;
    private ActionRecord actionRecord;

    @BeforeEach
    void setUp() {
        ordinaryUserRole = new Role(3, RoleEnum.USER, "Ordinary user", Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()));
        ordinaryUser =  new User(3, "John Sonny", "JohnySon", "123fea4", Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()), ordinaryUserRole);
        bankAccount = new BankAccount(1, ordinaryUser, "Camilla savings", BankType.BRIGHT_TOMMORROW, "21301060140000000212198127", BigDecimal.ZERO, "7924868944", List.of());
        actionRecord = new ActionRecord(1, bankAccount, 13, "51109020140000000712198128", "Rainbow St. 5a/21, Yellow City 98-119, New World", "Payment for work on construction", "Testing ActionRecord...", new BigDecimal("245.12"), LocalDateTime.now().plusDays(10));

    }

    @AfterEach
    void tearDown() {
        actionRecord = null;
        bankAccount =  null;
        ordinaryUser = null;
        ordinaryUserRole = null;
    }

    @Test
    void givenBanksTypes_whenGetBankType_thenGetBankType() {
        int bankCode = 13;

        BankType bankType = bankAccountService.getBankType(bankCode);

        assertEquals(BankType.STAR_BANK, bankType);
    }

    @Test
    void givenUser_whenGetUserBankAccounts_thenGetBankAccounts() {
        int userId = ordinaryUser.getId();

        when(bankAccountRepository.findByOwner(userId)).thenReturn(List.of(bankAccount));

        List<BankAccountDto> bankAccounts = bankAccountService.getUserBankAccounts(userId);

        assertEquals(1, bankAccounts.size());
        assertTrue(bankAccounts.stream().anyMatch(account -> account.equals(Extensions.asDto(bankAccount))), "Bank accounts list should contain existing bank account");
        verify(bankAccountRepository, times(1)).findByOwner(userId);
    }

    @Test
    void givenBankAccount_getBankAccount_thenGetBankAccount() {
        int bankAccountId = bankAccount.getId();

        when(bankAccountRepository.findById(bankAccountId)).thenReturn(Optional.of(bankAccount));

        BankAccountDto bankAccountDto = bankAccountService.getBankAccount(bankAccountId);

        assertNotNull(bankAccountDto, "Bank account should not be null");
        assertEquals(bankAccount, Extensions.asBankAccount(bankAccountDto), "Bank account result should contain existing bank account data");
        verify(bankAccountRepository, times(1)).findById(bankAccountId);
    }

    @Test
    void givenNewBankAccount_whenCreateBankAccount_thenGetCreatedBankAccount() {
        int userId = ordinaryUser.getId();
        String accountName = bankAccount.getAccountName();
        BankType bankType = bankAccount.getBankType();
        String accountNumber = bankAccount.getAccountNumber();
        String businessCode = bankAccount.getBusinessCode();

        when(bankAccountRepository.findByOwnerAndAccountName(userId, accountName)).thenReturn(Optional.empty());
        when(bankAccountRepository.existsByAccountNumber(accountNumber)).thenReturn(false);
        when(userService.findUser(userId)).thenReturn(Extensions.asDto(ordinaryUser));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        BankAccountCreationRequest bankAccountCreationRequest = new BankAccountCreationRequest(accountName, bankType, accountNumber, businessCode);

        BankAccountDto createdBankAccount = bankAccountService.createBankAccount(userId, bankAccountCreationRequest);

        assertNotNull(createdBankAccount, "Bank account should not be null");
        assertEquals(userId, createdBankAccount.owner().id(), "Bank account owner id should match " + userId);
        assertEquals(accountName, createdBankAccount.accountName(), "Bank account name should match " + accountName);
        assertEquals(bankType, createdBankAccount.bankType(), "Bank type should match " + bankType);
        assertEquals(accountNumber, createdBankAccount.accountNumber(), "Account number should match " + accountNumber);
        assertEquals(businessCode, createdBankAccount.businessCode(), "Business code should match " + businessCode);
        assertEquals(BigDecimal.ZERO, createdBankAccount.currentBalance(), "Business balance should be 0");
        assertEquals(0, createdBankAccount.actionRecords().size(), "Action records should be empty");
        verify(bankAccountRepository, times(1)).findByOwnerAndAccountName(userId, accountName);
        verify(bankAccountRepository, times(1)).existsByAccountNumber(accountNumber);
        verify(userService, times(1)).findUser(userId);
        verify(bankAccountRepository, times(1)).save(any(BankAccount.class));
    }

    @Test
    void givenBankAccount_whenUpdateBankAccountName() {
        int bankAccountId = bankAccount.getId();
        int ownerId = bankAccount.getOwner().getId();
        String newBankAccountName = "Camilla life";

        when(bankAccountRepository.findById(bankAccountId)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.findByOwnerAndAccountName(ownerId, newBankAccountName)).thenReturn(Optional.empty());

        BankAccountDto updatedBankAccount = bankAccountService.updateBankAccountName(bankAccountId, newBankAccountName);

        assertEquals(newBankAccountName, updatedBankAccount.accountName(), "Bank account name should match " + newBankAccountName);
        verify(bankAccountRepository, times(1)).findById(bankAccountId);
        verify(bankAccountRepository, times(1)).findByOwnerAndAccountName(ownerId, newBankAccountName);
    }

    @Test
    void givenBankAccount_whenRemoveBankAccount_thenBankAccountIsRemoved() {
        int bankAccountId = bankAccount.getId();

        when(bankAccountRepository.existsById(bankAccountId)).thenReturn(true);
        doAnswer(invocation -> {
            bankAccount = null;
            return null;
        }).when(bankAccountRepository).deleteById(bankAccountId);

        bankAccountService.removeBankAccount(bankAccountId);

        assertNull(bankAccount, "Bank account should be removed");
        verify(bankAccountRepository, times(1)).existsById(bankAccountId);
        verify(bankAccountRepository, times(1)).deleteById(bankAccountId);
    }

    @Test
    void givenBankAccount_whenRemoveBankAccountByUser_thenBankAccountIsRemoved() {
        int userId = bankAccount.getOwner().getId();
        String accountName = bankAccount.getAccountName();

        when(bankAccountRepository.findByOwnerAndAccountName(userId, accountName)).thenReturn(Optional.of(bankAccount));
        doAnswer(invocation -> {
            bankAccount = null;
            return null;
        }).when(bankAccountRepository).deleteById(any(Integer.class));

        bankAccountService.removeBankAccount(userId, accountName);

        assertNull(bankAccount, "Bank account should be removed");
        verify(bankAccountRepository, times(1)).findByOwnerAndAccountName(userId, accountName);
        verify(bankAccountRepository, times(1)).deleteById(any(Integer.class));
    }

    @Test
    void givenBankAccount_whenAddActionRecord_thenActionRecordIsAdded() {
        int bankAccountId =  bankAccount.getId();
        int externalBankCode = actionRecord.getExternalBankCode();
        String externalBankNumber =  actionRecord.getExternalBankNumber();
        String additionalAddressInfo = actionRecord.getAdditionalAddressInfo();
        String label =  actionRecord.getLabel();
        String description =  actionRecord.getDescription();
        BigDecimal fundsAmount = actionRecord.getFundsAmount();
        LocalDateTime registrationDateTime = actionRecord.getRegistrationDateTime();

        when(bankAccountRepository.findById(bankAccountId)).thenReturn(Optional.of(bankAccount));
        when(actionRecordRepository.save(any(ActionRecord.class))).thenReturn(actionRecord);

        ActionRecordCreationRequest actionRecordCreationRequest = new ActionRecordCreationRequest(externalBankCode, externalBankNumber, additionalAddressInfo, label, description, fundsAmount, registrationDateTime);

        ActionRecordDto actionRecordDto = bankAccountService.addActionRecord(bankAccountId, actionRecordCreationRequest);

        assertEquals(externalBankCode, actionRecordDto.externalBankCode(), "External bank code should match " + externalBankCode);
        assertEquals(externalBankNumber, actionRecordDto.externalBankNumber(), "Bank number should match " + externalBankNumber);
        assertEquals(label, actionRecordDto.label(), "Label should match " + label);
        assertEquals(description, actionRecordDto.description(), "Description should match " + description);
        assertEquals(fundsAmount, actionRecordDto.fundsAmount(), "Funds amount should match " + fundsAmount);
        assertEquals(registrationDateTime, actionRecordDto.registrationDateTime(), "Funds amount should match " + fundsAmount);
        verify(bankAccountRepository, times(1)).findById(bankAccountId);
        verify(actionRecordRepository, times(1)).save(any(ActionRecord.class));
    }

    @Test
    void givenActionRecord_whenRemoveActionRecord_thenActionRecordIsRemoved() {
        bankAccount.setActionRecords(List.of(actionRecord));
        bankAccount.setCurrentBalance(new BigDecimal(String.valueOf(actionRecord.getFundsAmount())));

        int actionRecordId  = actionRecord.getId();
        when(actionRecordRepository.findById(actionRecordId)).thenReturn(Optional.of(actionRecord));
        doAnswer(invocation -> {
            actionRecord = null;
            return null;
        }).when(actionRecordRepository).deleteById(actionRecordId);

        bankAccountService.removeActionRecord(actionRecordId);

        assertNull(actionRecord, "Action record should be removed");
        verify(actionRecordRepository, times(1)).findById(actionRecordId);
        verify(actionRecordRepository, times(1)).deleteById(actionRecordId);
    }
}