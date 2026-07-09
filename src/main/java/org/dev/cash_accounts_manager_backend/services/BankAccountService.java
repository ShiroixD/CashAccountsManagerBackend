package org.dev.cash_accounts_manager_backend.services;

import org.dev.cash_accounts_manager_backend.dtos.ActionRecordDto;
import org.dev.cash_accounts_manager_backend.dtos.BankAccountDto;
import org.dev.cash_accounts_manager_backend.dtos.PersonalInfoDto;
import org.dev.cash_accounts_manager_backend.dtos.UserDto;
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
import org.dev.cash_accounts_manager_backend.utils.Validators;
import org.springframework.stereotype.Service;

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
     *  Bank account data validation method
     *  @throws ValidationError in case of validation failure with description
     */
    private void checkBankAccountDto(BankAccountDto bankAccountDto) throws ValidationError {
        String validationResult = Validators.validate(bankAccountDto);

        if (!validationResult.isBlank()) {
            Logger.log("Validation error: \n" + validationResult);
            throw new ValidationError(validationResult);
        }
    }

    /**
     *  Bank account request data validation method
     *  @throws ValidationError in case of validation failure with description
     */
    private void checkBankAccountRequest(BankAccountCreationRequest request) throws ValidationError {
        String validationResult = Validators.validate(request);

        if (!validationResult.isBlank()) {
            Logger.log("Validation error: \n" + validationResult);
            throw new ValidationError(validationResult);
        }
    }

    /**
     *  Action record data validation method
     *  @throws ValidationError in case of validation failure with description
     */
    private void checkActionRecordDto(ActionRecordDto actionRecordDto) throws ValidationError {
        String validationResult = Validators.validate(actionRecordDto);

        if (!validationResult.isBlank()) {
            Logger.log("Validation error: \n" + validationResult);
            throw new ValidationError(validationResult);
        }
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
     *  @param userId @param userId user owner id
     *  @param request data to create bank account
     *  @throws DataAlreadyExistsException in case of adding duplicated data
     */
    public BankAccountDto createBankAccount(Integer userId, BankAccountCreationRequest request) throws DataAlreadyExistsException {
        checkBankAccountRequest(request);

        String accountNumber = request.accountNumber();

        if (bankAccountRepository.findByOwnerAndAccountName(userId, accountNumber).isPresent()) {
            throw new DataAlreadyExistsException(String.format("Bank account with name %s already exists for user with id %d",
                    request.accountName(), userId));
        }

        if (bankAccountRepository.existsByAccountNumber(accountNumber)) {
            throw new DataAlreadyExistsException(String.format("Bank account with number %s already exists", accountNumber));
        }

        UserDto userDto = userService.user(userId);

        BankAccount bankAccount = new BankAccount(
                Extensions.asUser(userDto),
                request.accountName(),
                request.bankType(),
                accountNumber,
                request.businessCode()
        );

        bankAccount = bankAccountRepository.save(bankAccount);

        return Extensions.asDto(bankAccountRepository.save(bankAccount));
    }

    /**
     *  Method for updating bank account name
     *  @param userId owner id
     *  @param newAccountName new account name
     *  @throws ValidationError in case of existing account with same name
     *  @throws NotFoundException in case bank account has not been found
     */
    public void updateBankAccountAccountName(int userId, String newAccountName) throws ValidationError, NotFoundException {
        Optional<BankAccount> foundBankAccount = bankAccountRepository.findByOwnerAndAccountName(userId, newAccountName);

        if (foundBankAccount.isEmpty()) {
            String message = this.getClass().getSimpleName() + " -> Bank account for user " + userId + " not found";
            throw new NotFoundException(message);
        }

        BankAccount bankAccount = foundBankAccount.get();
        bankAccount.setAccountName(newAccountName);

        bankAccountRepository.save(bankAccount);
    }

    /**
     *  Method for removing bank account by index
     *  @param id bank account id
     *  @throws NotFoundException in case of bank account not found
     */
    public void removeBankAccount(int id) throws NotFoundException {
        if (!bankAccountRepository.existsById(id)) {
            String message = this.getClass().getSimpleName() + " -> Bank account with id " + id + " not found";
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
    public void removeBankAccount(int userId, String accountName) throws ValidationError, NotFoundException {
        try {
            var bankAccount = bankAccountRepository.findByOwnerAndAccountName(userId, accountName);

            if (bankAccount.isEmpty()) {
                String message = this.getClass().getSimpleName() + " -> Could not find bank account for user " + userId + " with name " + accountName;
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
     *  @param actionRecordDto action record data
     */
    public void addActionRecord(int bankAccountId, ActionRecordDto actionRecordDto)
            throws NotFoundException, ActionDeniedException {
        var foundBankAccount = bankAccountRepository.findById(bankAccountId);

        if (foundBankAccount.isEmpty()) {
            String message = this.getClass().getSimpleName() + " -> Bank account with id " + bankAccountId + " not found";
            throw new NotFoundException(message);
        }

        checkActionRecordDto(actionRecordDto);

        var bankAccount = foundBankAccount.get();
        double afterActionRecordAddedBalance = bankAccount.getCurrentBalance() + actionRecordDto.fundsAmount();

        if (afterActionRecordAddedBalance < 0) {
            throw new ActionDeniedException("Action record cannot be added because of negative balance after execution");
        }

        bankAccount.setCurrentBalance(afterActionRecordAddedBalance);

        var actionRecords = bankAccount.getActionRecords();

        ActionRecord actionRecord = Extensions.asActionRecord(actionRecordDto);
        actionRecords.add(actionRecord);

        bankAccountRepository.save(bankAccount);
    }

    /**
     *  Method for removing action record from bank account
     *  @param bankAccountId bank account id
     *  @param actionRecordIndex order number of action record in bank account
     */
    public void removeActionRecord(int bankAccountId, int actionRecordIndex) throws NotFoundException {
        var foundBankAccount = bankAccountRepository.findById(bankAccountId);

        if (foundBankAccount.isEmpty()) {
            String message = this.getClass().getSimpleName() + " -> Bank account with id " + bankAccountId + " not found";
            throw new NotFoundException(message);
        }

        var bankAccount = foundBankAccount.get();

        if (actionRecordIndex < 0 || actionRecordIndex >= bankAccount.getActionRecords().size()) {
            String message = this.getClass().getSimpleName() + " -> Action record index " + actionRecordIndex + " is out of range";
            throw new NotFoundException(message);
        }

        ActionRecord actionRecord = bankAccount.getActionRecords().get(actionRecordIndex);
        double afterActionRecordRevertedBalance = bankAccount.getCurrentBalance() - actionRecord.getFundsAmount();

        if (afterActionRecordRevertedBalance < 0) {
            throw new ActionDeniedException("Action record cannot be removed because of negative balance after execution");
        }

        bankAccount.setCurrentBalance(afterActionRecordRevertedBalance);

        bankAccount.getActionRecords().remove(actionRecordIndex);
        bankAccountRepository.save(bankAccount);
    }
}
