package org.dev.cash_accounts_manager_backend.bootstrap.collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.dev.cash_accounts_manager_backend.models.Log;
import org.dev.cash_accounts_manager_backend.repositories.LogRepository;
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

/**
 * Class collecting logs from database and saving them to local file<br>
 * It is run on application start when profile "save" is active
 *
 * @author Fabian Frontczak
 */
@Component
@Profile("save")
@Order(4)
public class LogCollector implements ApplicationListener<ContextRefreshedEvent> {
    private final LogRepository logRepository;
    private final Environment env;

    /**
     * Class constructor with DI (dependency injection) for logs repository and environment properties
     * @param logRepository repository containing registered logs
     * @param env environment data containing properties
     */
    public LogCollector(LogRepository logRepository, Environment env) {
        this.logRepository = logRepository;
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
     *  Method getting all logs from repository and saving them to local file
     *  @throws IOException in case of file errors
     */
    private void save() throws IOException {

        String filesDirectory = env.getProperty("db_internal.data.location");
        Path directoryPath = Paths.get(filesDirectory);

        if (filesDirectory == null || filesDirectory.isBlank() || Files.notExists(directoryPath) || Files.isDirectory(directoryPath) == false) {
            filesDirectory = "./";
        }

        String fileName = env.getProperty("db_internal.data.logs.file");

        if (fileName == null || fileName.isBlank()) {
            fileName = "db_logs.json";
        }

        String separator = "/";
        Path savePath = Paths.get(filesDirectory + separator + fileName);

        int index = 1;
        while (Files.exists(savePath)) {
            fileName = "db_logs" + index + ".json";
            savePath = Paths.get(filesDirectory + separator + fileName);
            ++index;
        }

        List<Log> logs = (List<Log>) logRepository.findAll();

        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(savePath.toFile(), logs);
    }
}
