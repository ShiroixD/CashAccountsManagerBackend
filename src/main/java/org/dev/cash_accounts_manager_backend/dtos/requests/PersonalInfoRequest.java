package org.dev.cash_accounts_manager_backend.dtos.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.dev.cash_accounts_manager_backend.utils.RegexPatters;
import org.dev.cash_accounts_manager_backend.utils.ValidationMessageConsts;

/**
 * Personal info model for creation request
 *
 * @author Fabian Frontczak
 */
public record PersonalInfoRequest(
        @NotNull(message = "Owner cannot be null")
        @Pattern(
                regexp = RegexPatters.CHARACTERS_REGEX_PATTERN,
                message = "First name  " + ValidationMessageConsts.NOT_BLANK + " and " + ValidationMessageConsts.MUST_CONTAIN_ONLY_CHARACTERS
        )
        Integer ownerId,

        @NotBlank(message = "First name cannot be blank")
        @Pattern(
                regexp = RegexPatters.CHARACTERS_REGEX_PATTERN,
                message = "Last name  " + ValidationMessageConsts.NOT_BLANK + " and " + ValidationMessageConsts.MUST_CONTAIN_ONLY_CHARACTERS
        )
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Pattern(regexp = RegexPatters.EMAIL_REGEX_PATTERN, message = "Email must be of correct pattern")
        String lastName,

        @Email(regexp = RegexPatters.EMAIL_REGEX_PATTERN, message = "Email must be of correct pattern")
        String email,

        @NotBlank(message = "Phone number cannot be blank")
        @Pattern(
                regexp = RegexPatters.PHONE_REGEX_PATTERN,
                message = "Phone number  " + ValidationMessageConsts.NOT_BLANK + " must be of correct pattern"
        )
        String phoneNumber,

        @Nullable
        AddressRequest address,

        @NotBlank(message = "Personal code cannot be blank")
        String personalCode
) { }
