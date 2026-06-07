package org.dev.cash_accounts_manager_backend.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Static class for saving logs up to date to external file<br>
 *
 * @author Fabian Frontczak
 */
public class Logger {
    private static final String filePrefix = "logs";
    private static final String fileExtension = "txt";
    private static final String fileName;

    static {
        fileName = filePrefix + "." + fileExtension;
        File file = new File(fileName);

        if (!file.exists()) {
            boolean fileCreated;

            try {
                fileCreated = file.createNewFile();
            } catch (IOException e) {
                fileCreated = false;
            }

            if (!fileCreated) {
                System.out.println("Couldn't create log file\n");
            }
        }
    }

    public static void log(String message) {
        try(FileWriter writer = new FileWriter(fileName, true)) {
            String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
            writer.write("[ " + dateTime + " " + message + " ]" + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Couldn't write to log file\n");
        }
    }
}
