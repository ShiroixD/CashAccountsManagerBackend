package org.dev.cash_accounts_manager_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.dev.cash_accounts_manager_backend.dtos.BankAccountDto;
import org.dev.cash_accounts_manager_backend.dtos.PersonalInfoDto;
import org.dev.cash_accounts_manager_backend.dtos.UserDto;
import org.dev.cash_accounts_manager_backend.dtos.requests.BankAccountCreationRequest;
import org.dev.cash_accounts_manager_backend.enums.ActionsEnum;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.services.BankAccountService;
import org.dev.cash_accounts_manager_backend.services.LogService;
import org.dev.cash_accounts_manager_backend.services.PersonalDataService;
import org.dev.cash_accounts_manager_backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/bankAccount")
@RestController
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Bank Account API")
@ApiResponses(value = {
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
    public ResponseEntity<List<BankAccountDto>> bankAccounts(@RequestParam Integer userId) {
        UserDto userDto = userService.user(userId);
        var bankAccounts = bankAccountService.getUserBankAccounts(userDto.id());

        return ResponseEntity.ok(bankAccounts);
    }

    @Operation(summary = "Get bank account", description = "Returns chosen bank account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "531", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BankAccountDto> bankAccount(@PathVariable Integer id) {
        var bankAccounts = bankAccountService.getBankAccount(id);

        return ResponseEntity.ok(bankAccounts);
    }

    @Operation(summary = "Create bank account", description = "Creates bank account for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created personal info"),
            @ApiResponse(responseCode = "531", description = "User not found")
    })
    @PostMapping("/{userId}/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<BankAccountDto> addBankAccount(
            @PathVariable Integer userId,
            @Valid @RequestBody BankAccountCreationRequest bankAccountCreationRequest
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = ((User) authentication.getPrincipal());

        BankAccountDto newBankAccountDto = bankAccountService.createBankAccount(userId, bankAccountCreationRequest);
        logService.createLog(ActionsEnum.PERSONAL_INFO, authenticatedUser, "User " + authenticatedUser.getUsername(), "Added personal info for user id " + userId);

        return ResponseEntity.ok(newBankAccountDto);
    }
}