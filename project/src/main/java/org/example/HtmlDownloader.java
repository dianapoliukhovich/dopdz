package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HtmlDownloader {
    public static void downloadFile(String link, String directory) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == 200) {
                String fileName = Paths.get(url.getPath()).getFileName().toString();
                Path outputPath = Paths.get(directory, fileName);

                try (InputStream inputStream = connection.getInputStream();
                     OutputStream outputStream = Files.newOutputStream(outputPath)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    System.out.println("Файл " + fileName + " успешно загружен.");

                    readTitleFromFile(outputPath);
                }
            } else {
                System.out.println("Ошибка загрузки " + link + ": " + connection.getResponseCode());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void readTitleFromFile(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<title>") && line.contains("</title>")) {
                    System.out.println("Содержимое title: " + line);
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла " + path.getFileName() + ": " + e.getMessage());
        }
    }
}