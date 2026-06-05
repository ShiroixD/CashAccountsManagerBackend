package org.dev.cash_accounts_manager_backend.configs;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Internal database configuration class. It enables JPA repository
 *
 * @author Fabian Frontczak
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "org.dev.cash_accounts_manager_backend.repositories",
        entityManagerFactoryRef = "internalEntityManagerFactory",
        transactionManagerRef = "internalTransactionManager"
)
public class PersistenceInternalDatabaseConfiguration {
    public static final String INTERNAL = "internal";

    /**
     * Method providing database connection properties
     * @return {@link org.springframework.boot.jdbc.autoconfigure.DataSourceProperties}
     */
    @Primary
    @Bean("internalDataSourceProperties")
    @ConfigurationProperties("spring.datasource-internal")
    public DataSourceProperties internalDataSourceProperties() {

        return new DataSourceProperties();
    }

    /**
     * Method initializing database connection
     * @return {@link javax.sql.DataSource}
     */
    @Primary
    @Bean("internalDataSource")
    @ConfigurationProperties(prefix = "spring.datasource-internal")
    public DataSource internalDataSource(@Qualifier("internalDataSourceProperties") DataSourceProperties properties) {

        return properties.initializeDataSourceBuilder().build();
    }

    /**
     * Method providing entity manager factory for data models
     * @return {@link org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean}
     */
    @Primary
    @Bean("internalEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean internalEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("internalDataSource") DataSource dataSource) {

        return builder.dataSource(dataSource)
                .packages("org.dev.cash_accounts_manager_backend.models")
                .persistenceUnit(INTERNAL)
                .build();
    }

    /**
     * Method providing transaction manager
     * @return {@link org.springframework.transaction.PlatformTransactionManager}
     */
    @Primary
    @Bean("internalTransactionManager")
    @ConfigurationProperties("spring.jpa")
    public PlatformTransactionManager internalTransactionManager(
            @Qualifier("internalEntityManagerFactory") EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }
}
