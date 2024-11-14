import org.example.HtmlDownloader;
import org.junit.jupiter.api.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.util.Comparator;

import com.sun.net.httpserver.HttpServer;

import static org.junit.jupiter.api.Assertions.*;

public class HtmlDownloaderTest {

    private static final String TEST_URL = "http://localhost:8080/test.html";
    private static final String TEST_DIR = "testDir";
    private static final String TEST_FILE_CONTENT = "<html><head><title>Test Title</title></head></html>";
    private static HttpServer server;

    @BeforeAll
    public static void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/test.html", exchange -> {
            exchange.sendResponseHeaders(200, TEST_FILE_CONTENT.length());
            OutputStream os = exchange.getResponseBody();
            os.write(TEST_FILE_CONTENT.getBytes());
            os.close();
        });
        server.start();

        Files.createDirectories(Paths.get(TEST_DIR));
    }

    @AfterAll
    public static void tearDown() throws IOException {
        server.stop(0);
        Files.walk(Paths.get(TEST_DIR))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    public void testDownloadFile_Success() throws Exception {
        HtmlDownloader.downloadFile(TEST_URL, TEST_DIR);

        Path downloadedFilePath = Paths.get(TEST_DIR, "test.html");
        assertTrue(Files.exists(downloadedFilePath), "Файл не был загружен.");

        String content = new String(Files.readAllBytes(downloadedFilePath));
        assertTrue(content.contains("<title>Test Title"), "Содержимое файла некорректно.");
    }

    @Test
    public void testReadTitleFromFile() throws IOException {

        Path testFilePath = Paths.get(TEST_DIR, "test.html");
        Files.write(testFilePath, TEST_FILE_CONTENT.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        HtmlDownloader.readTitleFromFile(testFilePath);

        System.setOut(originalOut);
        String output = outputStream.toString();
        assertFalse(output.contains("Содержимое title: <head><title>Test Title</title></head>"), "Заголовок не найден в выводе.");
    }
}
