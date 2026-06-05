package org.dev.cash_accounts_manager_backend.bootstrap.seeders;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dev.cash_accounts_manager_backend.enums.RoleEnum;
import org.dev.cash_accounts_manager_backend.exceptions.UserRoleNotExist;
import org.dev.cash_accounts_manager_backend.models.Role;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.repositories.RoleRepository;
import org.dev.cash_accounts_manager_backend.repositories.UserRepository;
import org.dev.cash_accounts_manager_backend.utils.ResourceReader;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Class loading users from external local file<br>
 * It is run on application start
 *
 * @author Fabian Frontczak
 */
@Component
@Profile("load")
@Order(5)
public class UsersSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final Environment env;

    /**
     * Class constructor with DI (dependency injection) for roles, users repository and environment properties
     * @param roleRepository repository containing roles
     * @param userRepository repository containing users
     * @param env environment data containing properties
     */
    public UsersSeeder(
            RoleRepository roleRepository,
            UserRepository  userRepository,
            Environment env
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.env = env;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            this.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Method getting all users from external local file and saving them to database
     */
    private void load() throws IOException {
        String filesDirectory = env.getProperty("db_internal.data.location");
        Path directoryPath = Paths.get(filesDirectory);

        if (filesDirectory == null || filesDirectory.isBlank() || Files.notExists(directoryPath) || Files.isDirectory(directoryPath) == false) {
            filesDirectory = "./";
        }

        String fileName = env.getProperty("db_internal.data.users.file");

        if (fileName == null || fileName.isBlank()) {
            fileName = "db_users.json";
        }

        Path savePath = Paths.get(filesDirectory + "/" + fileName);

        if (Files.notExists(savePath)) {
            return;
        }

        String json = ResourceReader.readFileToString(savePath.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        List<User> users = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));

        userRepository.saveAll(users);
    }
}
