package com.brevitaz.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    public static String readFileFromClasspath(String url) {
        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader bufferedReader = null;

        try {
            ClassPathResource classPathResource = new ClassPathResource(url);
            InputStreamReader inputStreamReader = new InputStreamReader(classPathResource.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            LOGGER.debug(String.format("Failed to load file from url: %s: %s", url, e.getMessage()));
            return null;
        } finally {
            if (bufferedReader != null)
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LOGGER.debug(String.format("Unable to close buffered reader.. %s", e.getMessage()));
                }
        }

        return stringBuilder.toString();
    }
}
