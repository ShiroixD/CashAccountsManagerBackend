package org.dev.cash_accounts_manager_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.dev.cash_accounts_manager_backend.dtos.LoginResponse;
import org.dev.cash_accounts_manager_backend.dtos.LoginUserDto;
import org.dev.cash_accounts_manager_backend.enums.ActionsEnum;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.services.AuthenticationService;
import org.dev.cash_accounts_manager_backend.services.JwtService;
import org.dev.cash_accounts_manager_backend.services.LogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
@Tag(name = "Authentication API")
public class AuthenticationController {
    private final JwtService jwtService;
    private final LogService logService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, LogService logService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.logService = logService;
    }

    @Operation(summary = "Login account", description = "Login account and returns jwt token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "403", description = "Wrong username or password"),
            @ApiResponse(responseCode = "403", description = "Account is locked"),
            @ApiResponse(responseCode = "403", description = "Not authorized for resource"),
            @ApiResponse(responseCode = "403", description = "Invalid JWT signature"),
            @ApiResponse(responseCode = "403", description = "JWT token expired"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());

        logService.createLog(ActionsEnum.LOG_IN, authenticatedUser, "User " + authenticatedUser.getUsername(), "Logged in");

        return ResponseEntity.ok(loginResponse);
    }
}
