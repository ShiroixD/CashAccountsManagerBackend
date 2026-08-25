package org.dev.cash_accounts_manager_backend.services;

import org.dev.cash_accounts_manager_backend.dtos.LoginResponse;
import org.dev.cash_accounts_manager_backend.dtos.LoginUserDto;
import org.dev.cash_accounts_manager_backend.enums.ActionsEnum;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LogService logService;

    /**
     * Class constructor injecting dependencies and initializing necessary data
     */
    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            LogService logService
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.logService = logService;
    }

    /**
     *  Method for generation of user login authentication
     *  @param input login data consisting of username and password
     *  @return login response consisting of jwt token and expiration time
     */
    public LoginResponse authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.username(),
                        input.password()
                )
        );

        User authenticatedUser = userRepository.findByUsername(input.username())
                .orElseThrow();

        String jwtToken = jwtService.generateToken(authenticatedUser);

        logService.createLog(ActionsEnum.AUTH_TOKEN_CREATE, "User " + authenticatedUser.getUsername(), "Logged in");

        return new LoginResponse(jwtToken, jwtService.getExpirationTime());
    }
}
