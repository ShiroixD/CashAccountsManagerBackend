package org.dev.cash_accounts_manager_backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.dev.cash_accounts_manager_backend.dtos.UserDto;
import org.dev.cash_accounts_manager_backend.enums.BankType;

/**
 * Bank account model for creation request
 *
 * @author Fabian Frontczak
 */
public record BankAccountCreationRequest(
        @NotNull(message = "Owner cannot be null")
        UserDto owner,

        @NotBlank(message = "Account name cannot be blank")
        String accountName,

        BankType bankType,

        @NotNull(message = "Personal info cannot be null")
        Integer personalInfoId,

        @NotBlank(message = "Account number cannot be blank")
        String accountNumber,

        String businessCode
) { }
