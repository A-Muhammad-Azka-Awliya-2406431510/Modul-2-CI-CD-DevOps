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
class HomePageFunctionalTest {
    private static final String CHROME_VERSION_PROPERTY = "wdm.chromeVersion";
    private static final String PRODUCT_LIST_PATH = "/product/list";
    private static final String EXPECTED_TITLE = "Product List";
    private static final String EXPECTED_HEADER = "Product List";
    private static final By PAGE_HEADER = By.tagName("h2");
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)(?:\\.\\d+)+");
    private static final String[][] VERSION_COMMANDS = {
            {"chromium", "--version"},
            {"chromium-browser", "--version"},
            {"google-chrome", "--version"}
    };

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
    void pageTitle_isCorrect(ChromeDriver driver) {
        openProductListPage(driver);
        String pageTitle = driver.getTitle();

        assertEquals(EXPECTED_TITLE, pageTitle);
    }

    @Test
    void header_productList_isCorrect(ChromeDriver driver) {
        openProductListPage(driver);
        String headerText = driver.findElement(PAGE_HEADER).getText();

        assertEquals(EXPECTED_HEADER, headerText);
    }

    private void openProductListPage(ChromeDriver driver) {
        driver.get(buildBaseUrl() + PRODUCT_LIST_PATH);
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
