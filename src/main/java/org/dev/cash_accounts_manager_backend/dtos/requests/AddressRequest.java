package org.dev.cash_accounts_manager_backend.dtos.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.dev.cash_accounts_manager_backend.utils.RegexPatters;
import org.dev.cash_accounts_manager_backend.utils.ValidationMessageConsts;

/**
 * Address model for creation request
 *
 * @author Fabian Frontczak
 */
public record AddressRequest(
        @NotBlank(message = "Street cannot be blank")
        @Pattern(
                regexp = RegexPatters.CHARACTERS_REGEX_PATTERN,
                message = "Street " + ValidationMessageConsts.NOT_BLANK + " and " + ValidationMessageConsts.MUST_CONTAIN_ONLY_CHARACTERS_AND_NUMBERS
        )
        String street,

        @NotBlank(message = "House number cannot be blank")
        @Pattern(
                regexp = RegexPatters.CHARACTERS_NUMBERS_REGEX_PATTERN,
                message = "House " + ValidationMessageConsts.NOT_BLANK + " and " + ValidationMessageConsts.MUST_CONTAIN_ONLY_CHARACTERS_AND_NUMBERS
        )
        String houseNumber,

        @Nullable
        @Pattern(
                regexp = RegexPatters.CHARACTERS_NUMBERS_REGEX_PATTERN,
                message = "Apartment number " + ValidationMessageConsts.MUST_CONTAIN_ONLY_CHARACTERS_AND_NUMBERS
        )
        String apartmentNumber,

        @NotBlank(message = "City cannot be blank")
        @Pattern(
                regexp = RegexPatters.CHARACTERS_REGEX_PATTERN,
                message = "City " + ValidationMessageConsts.MUST_CONTAIN_ONLY_CHARACTERS
        )
        String city,

        @NotBlank(message = "State cannot be blank")
        @Pattern(
                regexp = RegexPatters.CHARACTERS_REGEX_PATTERN,
                message = "State " + ValidationMessageConsts.MUST_CONTAIN_ONLY_CHARACTERS
        )
        String state,

        @NotBlank(message = "Zip code cannot be blank")
        String zipCode,

        @NotBlank(message = "Country cannot be blank")
        @Pattern(
                regexp = RegexPatters.CHARACTERS_REGEX_PATTERN,
                message = "Country " + ValidationMessageConsts.MUST_CONTAIN_ONLY_CHARACTERS
        )
        String country
) { }
