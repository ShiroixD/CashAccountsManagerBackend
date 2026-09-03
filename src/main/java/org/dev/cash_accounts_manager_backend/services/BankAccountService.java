package org.dev.cash_accounts_manager_backend.services;

import org.dev.cash_accounts_manager_backend.dtos.ActionRecordDto;
import org.dev.cash_accounts_manager_backend.dtos.BankAccountDto;
import org.dev.cash_accounts_manager_backend.dtos.UserDto;
import org.dev.cash_accounts_manager_backend.dtos.requests.ActionRecordCreationRequest;
import org.dev.cash_accounts_manager_backend.dtos.requests.BankAccountCreationRequest;
import org.dev.cash_accounts_manager_backend.enums.BankType;
import org.dev.cash_accounts_manager_backend.exceptions.ActionDeniedException;
import org.dev.cash_accounts_manager_backend.exceptions.DataAlreadyExistsException;
import org.dev.cash_accounts_manager_backend.exceptions.NotFoundException;
import org.dev.cash_accounts_manager_backend.exceptions.ValidationError;
import org.dev.cash_accounts_manager_backend.models.account.ActionRecord;
import org.dev.cash_accounts_manager_backend.models.account.BankAccount;
import org.dev.cash_accounts_manager_backend.repositories.ActionRecordRepository;
import org.dev.cash_accounts_manager_backend.repositories.BankAccountRepository;
import org.dev.cash_accounts_manager_backend.utils.Extensions;
import org.dev.cash_accounts_manager_backend.utils.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Service logic implementation for bank accounts<br>
 * It provides business logic of actions with bank account and its registered records
 *
 * @author Fabian Frontczak
 */
@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final ActionRecordRepository actionRecordRepository;
    private final UserService userService;
    private Map<Integer, BankType> banksDictionary;

    /**
     * Class constructor injecting dependencies and initializing necessary data
     */
    public BankAccountService(BankAccountRepository bankAccountRepository, ActionRecordRepository actionRecordRepository, UserService userService) {
        this.bankAccountRepository = bankAccountRepository;
        this.actionRecordRepository = actionRecordRepository;
        this.userService = userService;
        banksDictionary = new HashMap<>();

        EnumSet.allOf(BankType.class).forEach(type -> banksDictionary.put(type.getCode(), type));
    }

    /**
     *  Method for getting bank type by its code
     *  @param code bank code number
     *  @return bank type corresponding to the code
     */
    public BankType  getBankType(int code) {
        return banksDictionary.get(code);
    }

    /**
     *  Method for getting all user bank accounts data
     *  @return list of all user bank accounts transformed to DTO
     */
    public List<BankAccountDto> getUserBankAccounts(Integer userId) {
        List<BankAccount> bankAccounts = bankAccountRepository.findByOwner(userId);

        return bankAccounts.stream().map(Extensions::asDto).toList();
    }

    /**
     *  Method for getting bank account data
     *  @return bank account transformed to DTO
     */
    public BankAccountDto getBankAccount(Integer bankAccountId) {
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId).orElseThrow(() -> new NotFoundException("Bank account with id " + bankAccountId + " not found"));

        return Extensions.asDto(bankAccount);
    }

    /**
     *  Method for creating new bank account
     *  @param userId userId user owner id
     *  @param request data to create bank account
     *  @throws DataAlreadyExistsException in case of adding duplicated data
     */
    @Transactional(
            label = "createBankAccount",
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor =  Exception.class
    )
    public BankAccountDto createBankAccount(Integer userId, BankAccountCreationRequest request) throws DataAlreadyExistsException {
        if (bankAccountRepository.findByOwnerAndAccountName(userId, request.accountName()).isPresent()) {
            throw new DataAlreadyExistsException(String.format("Bank account with name %s already exists for user with id %d",
                    request.accountName(), userId));
        }

        String accountNumber = request.accountNumber();

        if (bankAccountRepository.existsByAccountNumber(accountNumber)) {
            throw new DataAlreadyExistsException(String.format("Bank account with number %s already exists", accountNumber));
        }

        UserDto userDto = userService.findUser(userId);

        BankAccount bankAccount = new BankAccount(
                Extensions.asUser(userDto),
                request.accountName(),
                request.bankType(),
                accountNumber,
                request.businessCode()
        );

        bankAccount = bankAccountRepository.save(bankAccount);

        return Extensions.asDto(bankAccount);
    }

    /**
     *  Method for updating bank account name
     *  @param id bank account id
     *  @param newAccountName new account name
     *  @throws NotFoundException in case bank account has not been found
     *  @throws DataAlreadyExistsException in case bank account name is already taken by user's another bank account
     */
    @Transactional(
            label = "updateBankAccountName",
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor =  Exception.class
    )
    public BankAccountDto updateBankAccountName(int id, String newAccountName) throws NotFoundException, DataAlreadyExistsException {
        Optional<BankAccount> foundBankAccount = bankAccountRepository.findById(id);

        if (foundBankAccount.isEmpty()) {
            String message = "Bank account with ID " + id + " not found";
            throw new NotFoundException(message);
        }

        BankAccount bankAccount = foundBankAccount.get();
        int ownerId = bankAccount.getOwner().getId();

        if (bankAccountRepository.findByOwnerAndAccountName(ownerId, newAccountName).isPresent()) {
            String message = String.format(" Bank account with name %s already exists for user with id %d",
                    newAccountName, ownerId);
            throw new DataAlreadyExistsException(message);
        }

        bankAccount.setAccountName(newAccountName);

        return Extensions.asDto(bankAccount);
    }

    /**
     *  Method for removing bank account by index
     *  @param id bank account id
     *  @throws NotFoundException in case of bank account not found
     */
    @Transactional(
            label = "removeBankAccount",
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor =  Exception.class
    )
    public void removeBankAccount(int id) throws NotFoundException {
        if (!bankAccountRepository.existsById(id)) {
            String message = "Bank account with id " + id + " not found";
            throw new NotFoundException(message);
        }

        bankAccountRepository.deleteById(id);
    }

    /**
     *  Method for removing bank account by account name
     *  @param userId owner id
     *  @param accountName account name
     *  @throws ValidationError in case of bank account validation error
     *  @throws NotFoundException in case of personal info not found
     */
    @Transactional(
            label = "removeBankAccountByUser",
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor =  Exception.class
    )
    public void removeBankAccount(int userId, String accountName) throws ValidationError, NotFoundException {
        try {
            var bankAccount = bankAccountRepository.findByOwnerAndAccountName(userId, accountName);

            if (bankAccount.isEmpty()) {
                String message = "Could not find bank account for user " + userId + " with name " + accountName;
                throw new NotFoundException(message);
            }

            bankAccountRepository.deleteById(bankAccount.get().getId());
        } catch (ValidationError | NotFoundException e) {
            Logger.log(e.getMessage());
            throw e;
        }
    }

    /**
     *  Method for adding action record to bank account
     *  @param bankAccountId bank account id
     *  @param request action record request data
     */
    @Transactional(
            label = "addActionRecord",
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor =  Exception.class
    )
    public ActionRecordDto addActionRecord(int bankAccountId, ActionRecordCreationRequest request)
            throws NotFoundException, ActionDeniedException {
        var foundBankAccount = bankAccountRepository.findById(bankAccountId);

        if (foundBankAccount.isEmpty()) {
            String message = "Bank account with id " + bankAccountId + " not found";
            throw new NotFoundException(message);
        }

        var bankAccount = foundBankAccount.get();
        BigDecimal afterActionRecordAddedBalance = bankAccount.getCurrentBalance().add(request.fundsAmount());

        if (afterActionRecordAddedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ActionDeniedException("Action record cannot be added because of negative balance after execution");
        }

        bankAccount.setCurrentBalance(afterActionRecordAddedBalance);

        ActionRecord actionRecord = new ActionRecord(
                bankAccount,
                request.externalBankCode(),
                request.externalBankNumber(),
                request.additionalAddressInfo(),
                request.label(),
                request.description(),
                request.fundsAmount());

        actionRecord = actionRecordRepository.save(actionRecord);

        return Extensions.asDto(actionRecord);
    }

    /**
     *  Method for removing action record from bank account
     *  @param actionRecordId action record id
     */
    @Transactional(
            label = "removeActionRecord",
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor =  Exception.class
    )
    public void removeActionRecord(int actionRecordId) throws NotFoundException {
        Optional<ActionRecord> foundActionRecord = actionRecordRepository.findById(actionRecordId);

        if (foundActionRecord.isEmpty()) {
            String message = "Action record with id " + actionRecordId + " not found";
            throw new NotFoundException(message);
        }

        ActionRecord actionRecord = foundActionRecord.get();
        BankAccount bankAccount = actionRecord.getOwner();
        BigDecimal afterActionRecordRevertedBalance = bankAccount.getCurrentBalance().subtract(actionRecord.getFundsAmount());

        if (afterActionRecordRevertedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ActionDeniedException("Action record cannot be removed because of negative balance after execution");
        }

        bankAccount.setCurrentBalance(afterActionRecordRevertedBalance);
        actionRecordRepository.deleteById(actionRecord.getId());
    }
}
