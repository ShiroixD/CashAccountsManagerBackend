package org.dev.cash_accounts_manager_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.dev.cash_accounts_manager_backend.dtos.ActionRecordDto;
import org.dev.cash_accounts_manager_backend.dtos.BankAccountDto;
import org.dev.cash_accounts_manager_backend.dtos.UserDto;
import org.dev.cash_accounts_manager_backend.dtos.requests.ActionRecordCreationRequest;
import org.dev.cash_accounts_manager_backend.dtos.requests.BankAccountCreationRequest;
import org.dev.cash_accounts_manager_backend.dtos.requests.RemovedUserBankAccountRequest;
import org.dev.cash_accounts_manager_backend.enums.ActionsEnum;
import org.dev.cash_accounts_manager_backend.services.BankAccountService;
import org.dev.cash_accounts_manager_backend.services.LogService;
import org.dev.cash_accounts_manager_backend.services.PersonalDataService;
import org.dev.cash_accounts_manager_backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/bankAccount")
@RestController
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Bank Account API")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully executed"),
        @ApiResponse(responseCode = "401", description = "Authentication failed"),
        @ApiResponse(responseCode = "403", description = "Access denied / JWT signature is invalid / JWT token expired"),
        @ApiResponse(responseCode = "500", description = "Internal server error"),
})
public class BankAccountController {
    private final BankAccountService bankAccountService;
    private final PersonalDataService personalDataService;
    private final UserService userService;
    private final LogService logService;

    public BankAccountController(BankAccountService bankAccountService, PersonalDataService personalDataService, UserService userService, LogService logService) {
        this.bankAccountService = bankAccountService;
        this.personalDataService = personalDataService;
        this.userService = userService;
        this.logService = logService;
    }

    @Operation(summary = "Get user bank accounts", description = "Returns bank accounts of user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "531", description = "User not found")
    })
    @GetMapping("/user/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BankAccountDto>> bankAccounts(@RequestParam Integer userId) {
        UserDto userDto = userService.findUser(userId);
        var bankAccounts = bankAccountService.getUserBankAccounts(userDto.id());

        return ResponseEntity.ok(bankAccounts);
    }

    @Operation(summary = "Get bank account", description = "Returns chosen bank account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "531", description = "User not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BankAccountDto> bankAccount(@PathVariable Integer id) {
        var bankAccounts = bankAccountService.getBankAccount(id);

        return ResponseEntity.ok(bankAccounts);
    }

    @Operation(summary = "Create bank account", description = "Creates bank account for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created bank account"),
            @ApiResponse(responseCode = "531", description = "User not found")
    })
    @PostMapping("/{userId}/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<BankAccountDto> addBankAccount(
            @PathVariable Integer userId,
            @Valid @RequestBody BankAccountCreationRequest bankAccountCreationRequest
    ) {
        BankAccountDto newBankAccountDto = bankAccountService.createBankAccount(userId, bankAccountCreationRequest);

        logService.createLog(ActionsEnum.BANK_ACCOUNT_CREATE, "User with ID " + userId, "Added personal info for user id " + userId);

        return ResponseEntity.ok(newBankAccountDto);
    }

    @Operation(summary = "Update bank account", description = "Updates bank account name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated bank account name"),
            @ApiResponse(responseCode = "531", description = "User not found")
    })
    @PutMapping("/{id}/updateName")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BankAccountDto> updateBankAccountName(
            @PathVariable Integer id,
            @RequestBody @NotBlank String bankAccountName
    ) {
        BankAccountDto newBankAccountDto = bankAccountService.updateBankAccountName(id, bankAccountName);

        logService.createLog(ActionsEnum.BANK_ACCOUNT_MODIFY, "Bank account with ID " + id, "Updated name of bank account with id " + id);

        return ResponseEntity.ok(newBankAccountDto);
    }

    @Operation(summary = "Remove bank account", description = "Removes bank account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed bank account"),
            @ApiResponse(responseCode = "532", description = "Data not found")
    })
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> removeBankAccount(@PathVariable Integer id) {
        bankAccountService.removeBankAccount(id);

        logService.createLog(ActionsEnum.BANK_ACCOUNT_DELETE, "Bank account with ID " + id, "Removed bank account with ID " + id);

        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "Remove bank account by user and account name", description = "Removes bank account by user and account name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed bank account"),
            @ApiResponse(responseCode = "531", description = "User not found"),
            @ApiResponse(responseCode = "532", description = "Data not found")
    })
    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> removeBankAccountByUser(@Valid @RequestBody RemovedUserBankAccountRequest request) {
        int userId = request.ownerId();
        String accountName = request.accountName();

        bankAccountService.removeBankAccount(userId, accountName);

        logService.createLog(ActionsEnum.BANK_ACCOUNT_DELETE, "Bank account with name " + accountName + " for user with ID " + userId, "Removed bank account with name " + accountName + " for user with ID " + userId);

        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "Create action record", description = "Creates bank account action record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created bank account"),
            @ApiResponse(responseCode = "532", description = "Data not found")
    })
    @PostMapping("/{id}/addActionRecord")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ActionRecordDto> addActionRecord(
            @PathVariable Integer id,
            @Valid @RequestBody ActionRecordCreationRequest request
    ) {
        ActionRecordDto actionRecordDto = bankAccountService.addActionRecord(id, request);

        logService.createLog(ActionsEnum.BANK_ACCOUNT_MODIFY, "Bank account with ID " + id, "Added new action record for bank account with ID " + id);

        return ResponseEntity.ok(actionRecordDto);
    }

    @Operation(summary = "Remove action record", description = "Removes action record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed personal info"),
            @ApiResponse(responseCode = "532", description = "Data not found")
    })
    @DeleteMapping("/deleteActionRecord/{actionRecordId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> removeActionRecord(@PathVariable Integer actionRecordId) {
        bankAccountService.removeActionRecord(actionRecordId);

        logService.createLog(ActionsEnum.BANK_ACCOUNT_MODIFY, "Action record with ID " + actionRecordId, "Removed action record with ID " + actionRecordId);

        return ResponseEntity.ok("SUCCESS");
    }
}