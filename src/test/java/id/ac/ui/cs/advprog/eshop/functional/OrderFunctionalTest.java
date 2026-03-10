package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class OrderFunctionalTest {
    private static final String CHROME_VERSION_PROPERTY = "wdm.chromeVersion";
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)(?:\\.\\d+)+");
    private static final String[][] VERSION_COMMANDS = {
            {"chromium", "--version"},
            {"chromium-browser", "--version"},
            {"google-chrome", "--version"}
    };
    private static final String ORDER_CREATE_PATH = "/order/create";
    private static final String ORDER_HISTORY_PATH = "/order/history";
    private static final String EXPECTED_CREATE_TITLE = "Create New Order";
    private static final String EXPECTED_CREATE_HEADER = "Create New Order";
    private static final String EXPECTED_HISTORY_TITLE = "Order History";
    private static final String EXPECTED_HISTORY_HEADER = "Order History";
    private static final String EXPECTED_ORDER_LIST_TITLE = "Order List";
    private static final By PAGE_HEADER = By.tagName("h2");
    private static final By AUTHOR_INPUT = By.id("authorInput");
    private static final By SEARCH_BUTTON = By.id("searchOrderButton");

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    @BeforeAll
    static void configureChromeVersion() {
        if (System.getProperty(CHROME_VERSION_PROPERTY) != null) {
            return;
        }

        String browserVersion = detectBrowserMajorVersion();
        if (browserVersion != null) {
            System.setProperty(CHROME_VERSION_PROPERTY, browserVersion);
        }
    }

    @Test
    void orderCreatePage_titleIsCorrect(ChromeDriver driver) {
        driver.get(buildBaseUrl() + ORDER_CREATE_PATH);
        assertEquals(EXPECTED_CREATE_TITLE, driver.getTitle());
    }

    @Test
    void orderCreatePage_headerIsCorrect(ChromeDriver driver) {
        driver.get(buildBaseUrl() + ORDER_CREATE_PATH);
        assertEquals(EXPECTED_CREATE_HEADER, driver.findElement(PAGE_HEADER).getText());
    }

    @Test
    void orderHistoryPage_titleIsCorrect(ChromeDriver driver) {
        driver.get(buildBaseUrl() + ORDER_HISTORY_PATH);
        assertEquals(EXPECTED_HISTORY_TITLE, driver.getTitle());
    }

    @Test
    void orderHistoryPage_headerIsCorrect(ChromeDriver driver) {
        driver.get(buildBaseUrl() + ORDER_HISTORY_PATH);
        assertEquals(EXPECTED_HISTORY_HEADER, driver.findElement(PAGE_HEADER).getText());
    }

    @Test
    void orderHistorySubmit_redirectsToOrderList(ChromeDriver driver) {
        driver.get(buildBaseUrl() + ORDER_HISTORY_PATH);
        driver.findElement(AUTHOR_INPUT).sendKeys("Safira Sudrajat");
        driver.findElement(SEARCH_BUTTON).click();

        assertEquals(EXPECTED_ORDER_LIST_TITLE, driver.getTitle());
    }

    private String buildBaseUrl() {
        return String.format("%s:%d", testBaseUrl, serverPort);
    }

    private static String detectBrowserMajorVersion() {
        for (String[] command : VERSION_COMMANDS) {
            String versionOutput = readVersionOutput(command);
            if (versionOutput != null) {
                Matcher matcher = VERSION_PATTERN.matcher(versionOutput);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }
        return null;
    }

    private static String readVersionOutput(String[] command) {
        try {
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            if (process.waitFor() != 0) {
                return null;
            }
            return output.isBlank() ? null : output;
        } catch (IOException e) {
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
