package org.dev.cash_accounts_manager_backend.configs;

import org.dev.cash_accounts_manager_backend.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Spring application configuration containing beans for account authentication
 * (user derails service, password encoder, authentication manager and provider)
 *
 * @author Fabian Frontczak
 */
@Configuration
@EnableScheduling
public class ApplicationConfiguration {
    private final UserRepository userRepository;

    /**
     * Class constructor with DI (dependency injection) for users repository
     * @param userRepository repository containing users
     */
    public ApplicationConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Method providing user presence check in database
     * @return {@link org.springframework.security.core.userdetails.UserDetailsService}
     */
    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }

    /**
     * Method providing password encoder
     * @return {@link org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder}
     */
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Method providing authentication manager
     * @return {@link org.springframework.security.authentication.AuthenticationManager}
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Method providing authentication provider
     * @return {@link org.springframework.security.authentication.AuthenticationProvider}
     */
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}
