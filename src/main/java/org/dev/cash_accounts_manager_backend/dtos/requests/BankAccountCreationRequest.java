package org.dev.cash_accounts_manager_backend.dtos.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.dev.cash_accounts_manager_backend.dtos.UserDto;
import org.dev.cash_accounts_manager_backend.enums.BankType;
import org.dev.cash_accounts_manager_backend.utils.RegexPatters;
import org.dev.cash_accounts_manager_backend.utils.ValidationMessageConsts;

/**
 * Bank account model for creation request
 *
 * @author Fabian Frontczak
 */
public record BankAccountCreationRequest(
        @NotNull(message = "Owner cannot be null")
        UserDto owner,

        @NotBlank(message = "Account name cannot be blank")
        @Pattern(
                regexp = RegexPatters.CHARACTERS_NUMBERS_REGEX_PATTERN,
                message = "Account name " + ValidationMessageConsts.NOT_BLANK + " and " + ValidationMessageConsts.MUST_CONTAIN_ONLY_CHARACTERS_AND_NUMBERS
        )
        String accountName,

        BankType bankType,

        @NotNull(message = "Personal info cannot be null")
        Integer personalInfoId,

        @NotBlank(message = "Account number cannot be blank")
        @Pattern(
                regexp = RegexPatters.NUMBERS_REGEX_PATTERN,
                message = "Account number  " + ValidationMessageConsts.NOT_BLANK + " and " + ValidationMessageConsts.MUST_CONTAIN_ONLY_NUMBERS
        )
        String accountNumber,

        @Nullable
        @Pattern(
                regexp = RegexPatters.NUMBERS_REGEX_PATTERN,
                message = "Business code " + ValidationMessageConsts.NOT_BLANK + " and " + ValidationMessageConsts.MUST_CONTAIN_ONLY_NUMBERS
        )
        String businessCode
) { }
