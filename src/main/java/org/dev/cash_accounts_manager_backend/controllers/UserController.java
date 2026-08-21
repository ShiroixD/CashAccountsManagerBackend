package org.dev.cash_accounts_manager_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.dev.cash_accounts_manager_backend.dtos.PagedResponse;
import org.dev.cash_accounts_manager_backend.dtos.PasswordDto;
import org.dev.cash_accounts_manager_backend.dtos.UserDto;
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

@RestController
@RequestMapping("api/users")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User API")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully executed"),
        @ApiResponse(responseCode = "401", description = "Authentication failed"),
        @ApiResponse(responseCode = "403", description = "Access denied / JWT signature is invalid / JWT token expired"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
        /*@ApiResponse(responseCode = "513", description = "Role not found"),
        @ApiResponse(responseCode = "514", description = "User not found"),
        @ApiResponse(responseCode = "515", description = "User with given name exists"),
        @ApiResponse(responseCode = "531", description = "Username not found"),*/
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
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @Operation(summary = "Get account by username", description = "Returns account according to given username. Allowed for SUPER_ADMIN and ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "514", description = "User with username not found")
    })
    @GetMapping("/one")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserDto> user(@RequestParam @NotBlank(message = "Username is required") String username) {
        UserDto user = userService.findUser(username);
        UserDto currentUser = userService.getCurrentUser();

        if (currentUser.role().code().equals(RoleEnum.ADMIN) && user.role().code().equals(RoleEnum.SUPER_ADMIN)) {
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
        PagedResponse<UserDto> users = userService.allUsers(pageable);

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Update account password", description = "Changed password of currently logged account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "514", description = "User not found")
    })
    @PutMapping("/current/update/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateUserPassword(@Valid @RequestBody PasswordDto passwordDto) {
        UserDto currentUser = userService.getCurrentUser();

        userService.updatePassword(currentUser.username(), passwordDto.password());
        logService.createLog(ActionsEnum.ACCOUNT_MODIFY, currentUser, "User " + currentUser.username(), "Updated account password");

        return ResponseEntity.ok("SUCCESS");
    }
}
