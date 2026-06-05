package org.dev.cash_accounts_manager_backend;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Initializer loading application
 *
 * @author Fabian Frontczak
 */
public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CashAccountsManagerBackendApplication.class);
    }
}
