package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class EditDeleteProductFunctionalTest {
    private static final String CHROME_VERSION_PROPERTY = "wdm.chromeVersion";
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)(?:\\.\\d+)+");
    private static final String[][] VERSION_COMMANDS = {
            {"chromium", "--version"},
            {"chromium-browser", "--version"},
            {"google-chrome", "--version"}
    };
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(5);
    private static final By NAME_INPUT = By.id("nameInput");
    private static final By QUANTITY_INPUT = By.id("quantityInput");
    private static final By SAVE_BUTTON = By.cssSelector("button[type='submit']");
    private static final By DELETE_BUTTON = By.cssSelector("a.btn-danger");
    private static final By EDIT_BUTTON = By.cssSelector("a.btn-warning");
    private static final By EDIT_PAGE_HEADER = By.tagName("h3");
    private static final String PRODUCT_LIST_PATH = "/product/list";
    private static final String CREATE_PRODUCT_PATH = "/product/create";

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
    void editProduct_updatesNameAndQuantity(ChromeDriver driver) {
        String originalName = uniqueProductName("Original");
        String updatedName = uniqueProductName("Updated");

        createProduct(driver, originalName, 5);
        openProductListPage(driver);
        WebElement row = findRowByProductName(driver, originalName);
        row.findElement(EDIT_BUTTON).click();

        waitForEditPage(driver);
        updateProductForm(driver, updatedName, 12);
        waitForProductListPage(driver);

        WebElement updatedRow = findRowByProductName(driver, updatedName);
        assertRowHasQuantity(updatedRow, "12");
    }

    @Test
    void deleteProduct_removesProductFromList(ChromeDriver driver) {
        String productName = uniqueProductName("Delete");

        createProduct(driver, productName, 3);
        openProductListPage(driver);
        WebElement row = findRowByProductName(driver, productName);
        row.findElement(DELETE_BUTTON).click();

        Alert alert = waitForAlert(driver);
        alert.accept();

        waitForRowToDisappear(driver, row);
        assertTrue(driver.findElements(rowLocator(productName)).isEmpty());
    }

    private void createProduct(ChromeDriver driver, String name, int quantity) {
        openCreateProductPage(driver);
        driver.findElement(NAME_INPUT).sendKeys(name);
        driver.findElement(QUANTITY_INPUT).sendKeys(Integer.toString(quantity));
        driver.findElement(SAVE_BUTTON).click();
        waitForProductListPage(driver);
    }

    private void updateProductForm(ChromeDriver driver, String name, int quantity) {
        WebElement nameInput = driver.findElement(NAME_INPUT);
        nameInput.clear();
        nameInput.sendKeys(name);

        WebElement quantityInput = driver.findElement(QUANTITY_INPUT);
        quantityInput.clear();
        quantityInput.sendKeys(Integer.toString(quantity));

        driver.findElement(SAVE_BUTTON).click();
    }

    private void openProductListPage(ChromeDriver driver) {
        driver.get(buildBaseUrl() + PRODUCT_LIST_PATH);
        waitForProductListPage(driver);
    }

    private void openCreateProductPage(ChromeDriver driver) {
        driver.get(buildBaseUrl() + CREATE_PRODUCT_PATH);
    }

    private void waitForProductListPage(ChromeDriver driver) {
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.titleIs("Product List"));
    }

    private void waitForEditPage(ChromeDriver driver) {
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.textToBePresentInElementLocated(EDIT_PAGE_HEADER, "Edit Product"));
    }

    private Alert waitForAlert(ChromeDriver driver) {
        return new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.alertIsPresent());
    }

    private void waitForRowToDisappear(ChromeDriver driver, WebElement row) {
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.stalenessOf(row));
    }

    private WebElement findRowByProductName(ChromeDriver driver, String name) {
        By locator = rowLocator(name);
        return new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private By rowLocator(String name) {
        return By.xpath("//tr[td[normalize-space()='" + name + "']]");
    }

    private void assertRowHasQuantity(WebElement row, String expectedQuantity) {
        List<WebElement> cells = row.findElements(By.tagName("td"));
        assertTrue(cells.size() >= 2);
        assertEquals(expectedQuantity, cells.get(1).getText().trim());
    }

    private String buildBaseUrl() {
        return String.format("%s:%d", testBaseUrl, serverPort);
    }

    private static String uniqueProductName(String prefix) {
        return prefix + "-" + UUID.randomUUID();
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
