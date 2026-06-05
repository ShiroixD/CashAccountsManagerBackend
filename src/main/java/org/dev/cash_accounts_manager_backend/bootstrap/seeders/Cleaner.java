package org.dev.cash_accounts_manager_backend.bootstrap.seeders;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Class deleting whole database<br>
 * It is run on application start when profile "clean" is active
 *
 * @author Fabian Frontczak
 */
@Component
@Profile("clean")
@Order(1)
@Transactional
public class Cleaner implements ApplicationListener<ContextRefreshedEvent> {
    private final EntityManager entityManager;

    /**
     * Class constructor with DI (dependency injection) for application entity manager
     * @param entityManager application entity manager
     */
    public Cleaner(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {}

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Query dropQuery = entityManager.createNativeQuery("drop database internal");
        dropQuery.executeUpdate();
        event.getApplicationContext().close();
    }
}
