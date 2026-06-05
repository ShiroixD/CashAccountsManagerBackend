package org.dev.cash_accounts_manager_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main spring boot application
 *
 * @author Fabian Frontczak
 */
@SpringBootApplication
public class CashAccountsManagerBackendApplication {

    /**
     * Method providing authentication provider
     * @param args execution parameters
     */
    public static void main(String[] args) {
        SpringApplication.run(CashAccountsManagerBackendApplication.class, args);
    }

}
