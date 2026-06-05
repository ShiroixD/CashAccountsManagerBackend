package org.dev.cash_accounts_manager_backend.utils;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Resource reader class for loading file into string
 *
 * @author Fabian Frontczak
 */
public class ResourceReader {
    /**
     * Method getting data from resource and loading it into string
     * @param resource resource
     * @return {@link java.lang.String}
     */
    public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Method loading file resource and converting it to string
     * @param path file path
     * @return {@link java.lang.String}
     */
    public static String readFileToString(String path) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("file:" + path);
        return asString(resource);
    }
}
