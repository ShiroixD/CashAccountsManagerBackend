package org.dev.cash_accounts_manager_backend.bootstrap.seeders;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dev.cash_accounts_manager_backend.models.Log;
import org.dev.cash_accounts_manager_backend.repositories.LogRepository;
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

/**
 * Class loading logs from external local file<br>
 * It is run on application start
 *
 * @author Fabian Frontczak
 */
@Component
@Profile("load")
@Order(7)
public class LogsSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final LogRepository logRepository;
    private final Environment env;

    /**
     * Class constructor with DI (dependency injection) for logs repository and environment properties
     * @param logRepository repository containing logs
     */
    public LogsSeeder(LogRepository logRepository, Environment env) {
        this.logRepository = logRepository;
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
     *  Method getting all logs from external local file and saving them to database
     *  @throws IOException in case of file errors
     */
    private void load() throws IOException {

        String filesDirectory = env.getProperty("db_internal.data.location");
        Path directoryPath = Paths.get(filesDirectory);

        if (filesDirectory == null || filesDirectory.isBlank() || Files.notExists(directoryPath) || Files.isDirectory(directoryPath) == false) {
            filesDirectory = "./";
        }

        String fileName = env.getProperty("db_internal.data.logs.file");

        if (fileName == null || fileName.isBlank()) {
            fileName = "db_logs.json";
        }

        Path savePath = Paths.get(filesDirectory + "/" + fileName);

        if (Files.notExists(savePath)) {
            return;
        }

        String json = ResourceReader.readFileToString(savePath.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        List<Log> logs = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Log.class));

        logRepository.saveAll(logs);
    }
}
