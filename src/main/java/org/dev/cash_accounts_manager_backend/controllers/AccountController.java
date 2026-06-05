package org.dev.cash_accounts_manager_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.dev.cash_accounts_manager_backend.dtos.*;
import org.dev.cash_accounts_manager_backend.enums.ActionsEnum;
import org.dev.cash_accounts_manager_backend.enums.RoleEnum;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.services.LogService;
import org.dev.cash_accounts_manager_backend.services.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/accounts")
@RestController
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Accounts API")
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Authentication failed"),
        @ApiResponse(responseCode = "403", description = "Access denied / JWT signature is invalid / JWT token expired"),
        @ApiResponse(responseCode = "500", description = "Internal server error"),
})
public class AccountController {
    private final UserService userService;
    private final LogService logService;

    public AccountController(UserService userService, LogService logService) {
        this.userService = userService;
        this.logService = logService;

    }

    @Operation(summary = "Get all account roles", description = "Returns a list of existing role data. Allowed for SUPER_ADMIN and ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    @GetMapping("/roles")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<RoleDto>> roles() {
        return ResponseEntity.ok(userService.allRoles());
    }

    @Operation(summary = "Create admin account", description = "Creates account with role ADMIN. Allowed for SUPER_ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created account"),
            @ApiResponse(responseCode = "513", description = "ADMIN role not found"),
            @ApiResponse(responseCode = "515", description = "Username already taken")
    })
    @PostMapping("/create/admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserDto> createAdmin(@Valid @RequestBody RegisterUserDto registerUserDto) {
        UserDto registeredUser = create(registerUserDto, RoleEnum.ADMIN);

        return ResponseEntity.ok(registeredUser);
    }

    @Operation(summary = "Create user account", description = "Creates account with role USER. Allowed for SUPER_ADMIN and ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created account"),
            @ApiResponse(responseCode = "513", description = "USER role not found"),
            @ApiResponse(responseCode = "515", description = "Username already taken")
    })
    @PostMapping("/create/user")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody RegisterUserDto registerUserDto) {
        UserDto registeredUser = create(registerUserDto, RoleEnum.USER);

        return ResponseEntity.ok(registeredUser);
    }

    private UserDto create(RegisterUserDto registerUserDto, RoleEnum roleEnum) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User authenticatedUser = ((User) authentication.getPrincipal());

        UserDto registeredUser = userService.create(registerUserDto, roleEnum);

        logService.createLog(ActionsEnum.ACCOUNT_CREATE, authenticatedUser, "User " + registerUserDto.username(), "Created account with role " + roleEnum);

        return registeredUser;
    }

    @Operation(summary = "Deactivate account", description = "Deactivates account. Allowed for SUPER_ADMIN and ADMIN according to hierarchy")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deactivated account"),
            @ApiResponse(responseCode = "514", description = "Account not found")
    })
    @PostMapping("/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UsernameDto> deactivateAccount(@Valid @RequestBody UsernameDto usernameDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User authenticatedUser = ((User) authentication.getPrincipal());

        UserDto currentUserDto = authenticatedUser.toDto();
        UserDto existingUserDto = userService.user(usernameDto.username());

        RoleEnum currentUserRoleEnum = currentUserDto.role().code();
        RoleEnum userToDeleteRoleEnum = existingUserDto.role().code();

        if (userToDeleteRoleEnum == RoleEnum.SUPER_ADMIN) {
            throw new AccessDeniedException(RoleEnum.SUPER_ADMIN + " cannot be deactivated");
        }

        if (currentUserRoleEnum != RoleEnum.SUPER_ADMIN && (userToDeleteRoleEnum == RoleEnum.SUPER_ADMIN || currentUserRoleEnum == userToDeleteRoleEnum)) {
            throw new AccessDeniedException(existingUserDto.username() + " cannot be deactivated by " + currentUserDto.username() + ". Required higer permissions");
        }

        userService.deactivate(existingUserDto.username());

        logService.createLog(ActionsEnum.ACCOUNT_DELETE, authenticatedUser, "User " + existingUserDto.username(), "Deactivated account " + existingUserDto.username() + " with role " + existingUserDto.role().code());

        return ResponseEntity.ok(usernameDto);
    }

    @Operation(summary = "Update account info", description = "Updates all account data. Allowed for SUPER_ADMIN and ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated account"),
            @ApiResponse(responseCode = "514", description = "Account not username not found"),
            @ApiResponse(responseCode = "531", description = "Account with id not found")
    })
    @PostMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserDto> updateAccountInfo(@PathVariable Integer id, @RequestBody UpdateUserDto updateUserDto) {
        UserDto userDto = update(id, updateUserDto);

        return ResponseEntity.ok(userDto);
    }

    private UserDto update(Integer id, UpdateUserDto updateUserDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User authenticatedUser = ((User) authentication.getPrincipal());

        UserDto currentUserDto = authenticatedUser.toDto();
        UserDto existingUserDto = userService.user(id);

        RoleEnum currentUserRoleEnum = currentUserDto.role().code();
        RoleEnum userToUpdateRoleEnum = existingUserDto.role().code();

        if (currentUserRoleEnum != RoleEnum.SUPER_ADMIN && (userToUpdateRoleEnum == RoleEnum.SUPER_ADMIN || currentUserRoleEnum == userToUpdateRoleEnum)) {
            throw new AccessDeniedException(existingUserDto.username() + " cannot be updated by " + currentUserDto.username() + ". Required higer permissions");
        }

        existingUserDto = userService.update(id, updateUserDto);

        logService.createLog(ActionsEnum.ACCOUNT_MODIFY, authenticatedUser, "User " + existingUserDto.username(), "Updated account with role " + existingUserDto.role().code());

        return existingUserDto;
    }

    @Operation(summary = "Change account role", description = "Updates account role. Only super admin is allowed to execute this")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully changed account role"),
            @ApiResponse(responseCode = "513", description = "Role not found"),
            @ApiResponse(responseCode = "514", description = "Account not found"),
            @ApiResponse(responseCode = "531", description = "Account with id not found")
    })
    @PostMapping("/{id}/changeRole")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserDto> changeRole(@PathVariable Integer id, @Valid @RequestBody UserRoleUpdate userRoleUpdate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User authenticatedUser = ((User) authentication.getPrincipal());

        UserDto existingUserDto = userService.user(id);

        RoleEnum userToUpdateRoleEnum = existingUserDto.role().code();

        if (userRoleUpdate.role().equals(RoleEnum.SUPER_ADMIN)) {
            throw new AccessDeniedException(RoleEnum.SUPER_ADMIN + " role cannot be granted");
        }

        if (existingUserDto.role().code().equals(RoleEnum.SUPER_ADMIN)) {
            throw new AccessDeniedException("Account with role " + RoleEnum.SUPER_ADMIN + " cannot be updated");
        }

        existingUserDto = userService.changeRole(existingUserDto.username(), userRoleUpdate.role());

        logService.createLog(ActionsEnum.ACCOUNT_MODIFY, authenticatedUser, "User " + existingUserDto.username(), "Changed user account role from " + userToUpdateRoleEnum + " to " + existingUserDto.role().code());

        return ResponseEntity.ok(existingUserDto);
    }

    @Operation(summary = "Get all logs", description = "Returns a list of existing logs. Allowed for SUPER_ADMIN and ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    @GetMapping("/logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<LogDto>> allLogs() {
        return ResponseEntity.ok(logService.allLogs());
    }

    @Operation(summary = "Get all logs paginated", description = "Returns a paginated list of existing logs. Allowed for SUPER_ADMIN and ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    @GetMapping("/logsPaginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PagedResponse<LogDto>> allLogsPaginated(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(logService.allLogs(pageable));
    }
}
