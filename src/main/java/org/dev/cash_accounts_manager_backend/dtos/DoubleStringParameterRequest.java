package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO model for request containing two string parameters
 *
 * @author Fabian Frontczak
 */
public record DoubleStringParameterRequest(
        @NotBlank(message = "String sentence one is required")
        String sentenceOne,

        @NotBlank(message = "String sentence two is required")
        String sentenceTwo
) { }
