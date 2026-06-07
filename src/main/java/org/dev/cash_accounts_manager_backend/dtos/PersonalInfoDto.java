package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * {@link org.dev.cash_accounts_manager_backend.models.person.PersonalInfo} DTO model
 *
 * @author Fabian Frontczak
 */
public record PersonalInfoDto(
        @NotBlank(message = "First name cannot be blank")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        String lastName,

        String email,

        @NotBlank(message = "Phone number cannot be blank")
        String phoneNumber,

        AddressDto address,

        @NotBlank(message = "Personal code cannot be blank")
        String personalCode
) { }
