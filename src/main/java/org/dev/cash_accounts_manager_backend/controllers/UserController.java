package org.dev.cash_accounts_manager_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.dev.cash_accounts_manager_backend.dtos.PagedResponse;
import org.dev.cash_accounts_manager_backend.dtos.PasswordDto;
import org.dev.cash_accounts_manager_backend.dtos.UserDto;
import org.dev.cash_accounts_manager_backend.dtos.UsernameDto;
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

@RequestMapping("api/users")
@RestController
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User API")
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Authentication failed"),
        @ApiResponse(responseCode = "403", description = "Access denied / JWT signature is invalid / JWT token expired"),
        @ApiResponse(responseCode = "500", description = "Internal server error"),
})
public class UserController {
    private final UserService userService;
    private final LogService logService;

    public UserController(UserService userService, LogService logService) {
        this.userService = userService;
        this.logService = logService;
    }

    @Operation(summary = "Get current account", description = "Returns currently logged account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser.toDto());
    }

    @Operation(summary = "Get account by username", description = "Returns account according to given username. Allowed for SUPER_ADMIN and ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "514", description = "Account not found")
    })
    @PostMapping("/one")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserDto> user(@Valid @RequestBody UsernameDto usernameDto) {
        UserDto user = userService.user(usernameDto.username());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole().getCode().equals(RoleEnum.ADMIN) && user.role().code().equals(RoleEnum.SUPER_ADMIN)) {
            throw new AccessDeniedException(RoleEnum.ADMIN + " cannot check " + RoleEnum.SUPER_ADMIN + " data");
        }

        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get all accounts", description = "Returns a list of existing accounts. Allowed for SUPER_ADMIN and ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<UserDto>> allUsers() {
        List<UserDto> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get all accounts paginated", description = "Returns a paginated list of existing accounts. Allowed for SUPER_ADMIN and ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    @GetMapping("/allPaginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PagedResponse<UserDto>> allUsersPaginated(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        PagedResponse<UserDto> users = userService.allUsers(pageable, false);

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Update account password", description = "Changed password of currently logged account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "514", description = "Account not found")
    })
    @PostMapping("/current/update/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateUserPassword(@Valid @RequestBody PasswordDto passwordDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User authenticatedUser = ((User) authentication.getPrincipal());

        userService.updatePassword(authenticatedUser.getUsername(), passwordDto.password());

        logService.createLog(ActionsEnum.ACCOUNT_MODIFY, authenticatedUser, "User " + authenticatedUser.getUsername(), "Updated account password");

        return ResponseEntity.ok("SUCCESS");
    }
}
