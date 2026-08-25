package org.dev.cash_accounts_manager_backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.dev.cash_accounts_manager_backend.utils.RegexPatters;
import org.dev.cash_accounts_manager_backend.utils.ValidationMessageConsts;

/**
 * Request model for removing bank account by user id and account name
 *
 * @author Fabian Frontczak
 */
public record RemovedUserBankAccountRequest(
        @NotNull(message = "Owner cannot be null")
        Integer ownerId,

        @NotBlank(message = "Account name cannot be blank")
        @Pattern(
                regexp = RegexPatters.CHARACTERS_REGEX_PATTERN,
                message = "Account name  " + ValidationMessageConsts.NOT_BLANK + " and " + ValidationMessageConsts.MUST_CONTAIN_ONLY_CHARACTERS
        )
        String accountName
) { }
