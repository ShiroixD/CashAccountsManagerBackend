package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.dev.cash_accounts_manager_backend.utils.RegexPatters;

/**
 * {@link org.dev.cash_accounts_manager_backend.models.person.PersonalInfo} DTO model
 *
 * @author Fabian Frontczak
 */
public record PersonalInfoDto(
        @NotNull(message = "Id cannot be null")
        Integer id,

        @NotNull(message = "Owner cannot be null")
        UserDto owner,

        @NotBlank(message = "First name cannot be blank")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        String lastName,

        @Email(regexp = RegexPatters.EMAIL_REGEX_PATTERN, message = "Email must be of correct pattern")
        String email,

        @NotBlank(message = "Phone number cannot be blank")
        String phoneNumber,

        @NotNull
        AddressDto address,

        @NotBlank(message = "Personal code cannot be blank")
        String personalCode
) { }
