package org.dev.cash_accounts_manager_backend.bootstrap.collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.dev.cash_accounts_manager_backend.enums.RoleEnum;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.repositories.UserRepository;
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
import java.util.stream.Collectors;

/**
 * Class collecting users from database and saving them to local file<br>
 * It is run on application start when profile "save" is active
 *
 * @author Fabian Frontczak
 */
@Component
@Profile("save")
@Order(4)
public class UserCollector implements ApplicationListener<ContextRefreshedEvent> {
    private final UserRepository userRepository;
    private final Environment env;

    /**
     * Class constructor with DI (dependency injection) for users repository and environment properties
     * @param userRepository repository containing users
     * @param env environment data containing properties
     */
    public UserCollector(UserRepository userRepository, Environment env) {
        this.userRepository = userRepository;
        this.env = env;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            this.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Method getting all users from repository and saving them to local file
     *  @throws IOException in case of file errors
     */
    private void save() throws IOException {

        String filesDirectory = env.getProperty("db_internal.data.location");
        Path directoryPath = Paths.get(filesDirectory);

        if (filesDirectory == null || filesDirectory.isBlank() || Files.notExists(directoryPath) || Files.isDirectory(directoryPath) == false) {
            filesDirectory = "./";
        }

        String fileName = env.getProperty("db_internal.data.users.file");

        if (fileName == null || fileName.isBlank()) {
            fileName = "db_users.json";
        }

        String separator = "/";
        Path savePath = Paths.get(filesDirectory + separator + fileName);

        int index = 1;
        while (Files.exists(savePath)) {
            fileName = "db_users" + index + ".json";
            savePath = Paths.get(filesDirectory + separator + fileName);
            ++index;
        }

        List<User> users = ((List<User>) userRepository.findAll()).stream()
                .filter(user -> !user.getRole().getCode().equals(RoleEnum.SUPER_ADMIN))
                .collect(Collectors.toList());

        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(savePath.toFile(), users);
    }
}
