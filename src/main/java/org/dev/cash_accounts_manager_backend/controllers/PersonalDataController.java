package org.dev.cash_accounts_manager_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.dev.cash_accounts_manager_backend.dtos.PersonalInfoDto;
import org.dev.cash_accounts_manager_backend.dtos.UserDto;
import org.dev.cash_accounts_manager_backend.dtos.requests.PersonalInfoRequest;
import org.dev.cash_accounts_manager_backend.enums.ActionsEnum;
import org.dev.cash_accounts_manager_backend.services.LogService;
import org.dev.cash_accounts_manager_backend.services.PersonalDataService;
import org.dev.cash_accounts_manager_backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/personalData")
@RestController
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Personal Info API")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully executed"),
        @ApiResponse(responseCode = "401", description = "Authentication failed"),
        @ApiResponse(responseCode = "403", description = "Access denied / JWT signature is invalid / JWT token expired"),
        @ApiResponse(responseCode = "500", description = "Internal server error"),
})
public class PersonalDataController {
    private final PersonalDataService personalDataService;
    private final UserService userService;
    private final LogService logService;

    public  PersonalDataController(PersonalDataService personalDataService, UserService userService, LogService logService) {
        this.personalDataService = personalDataService;
        this.userService = userService;
        this.logService = logService;
    }

    @Operation(summary = "Get user personal data", description = "Returns personal info data connected to chosen user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "531", description = "User not found")
    })
    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PersonalInfoDto> personalInfo(@PathVariable Integer userId) {
        PersonalInfoDto personalInfoDto = personalDataService.getUserPersonalInfo(userId);

        return ResponseEntity.ok(personalInfoDto);
    }

    @Operation(summary = "Get current user personal data", description = "Returns current user personal info data ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "531", description = "User not found")
    })
    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PersonalInfoDto> currentUserPersonalInfo() {
        UserDto currentUser = userService.getCurrentUser();
        PersonalInfoDto personalInfoDto = personalDataService.getUserPersonalInfo(currentUser.id());

        return ResponseEntity.ok(personalInfoDto);
    }

    @Operation(summary = "Create user personal info", description = "Creates personal info connected to user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created personal info"),
            @ApiResponse(responseCode = "531", description = "User not found")
    })
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PersonalInfoDto> addPersonalInfo(@Valid @RequestBody PersonalInfoRequest request) {
        Integer userId = request.ownerId();
        PersonalInfoDto newPersonalInfoDto = personalDataService.addPersonalInfo(userId, request);

        logService.createLog(ActionsEnum.PERSONAL_INFO, "User with ID " + userId, "Added personal info for user id " + userId);

        return ResponseEntity.ok(newPersonalInfoDto);
    }

    @Operation(summary = "Update user personal info", description = "Updates personal info connected to user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated personal info"),
            @ApiResponse(responseCode = "531", description = "User not found"),
            @ApiResponse(responseCode = "532", description = "Data not found")
    })
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PersonalInfoDto> updatePersonalInfo(@Valid @RequestBody PersonalInfoRequest request) {
        Integer userId = request.ownerId();
        PersonalInfoDto personalInfoDto = personalDataService.updatePersonalInfo(userId,  request);

        logService.createLog(ActionsEnum.PERSONAL_INFO, "User with ID " + userId, "Updated personal info for user id " + userId);

        return ResponseEntity.ok(personalInfoDto);
    }

    @Operation(summary = "Remove user personal info", description = "Removes personal info connected to user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed personal info"),
            @ApiResponse(responseCode = "531", description = "User not found"),
            @ApiResponse(responseCode = "532", description = "Data not found")
    })
    @DeleteMapping("/{userId}/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> removePersonalInfo(@PathVariable Integer userId) {
        personalDataService.removePersonalInfo(userId);

        logService.createLog(ActionsEnum.PERSONAL_INFO, "User with ID " + userId, "Removed personal info for user id " + userId);

        return ResponseEntity.ok("SUCCESS");
    }
}
