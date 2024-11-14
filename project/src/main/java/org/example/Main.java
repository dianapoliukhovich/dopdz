package org.example;

import java.util.Scanner;
import java.util.stream.Stream;

import static org.example.HtmlDownloader.downloadFile;

public class Main {

    public static void main(String[] args) {
        System.out.println("Введите ссылки: ");
        Scanner scanner = new Scanner(System.in);
        String[] links = scanner.nextLine().split(";");
        System.out.println("Введите директорию: ");
        String directory = scanner.nextLine();

        Stream.of(links).parallel().forEach(link -> {
            downloadFile(link.trim(), directory);
        });
    }
}
