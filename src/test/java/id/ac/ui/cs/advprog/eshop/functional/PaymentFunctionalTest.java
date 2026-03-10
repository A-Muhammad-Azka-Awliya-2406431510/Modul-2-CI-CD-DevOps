package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class PaymentFunctionalTest {
    private static final String CHROME_VERSION_PROPERTY = "wdm.chromeVersion";
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)(?:\\.\\d+)+");
    private static final String[][] VERSION_COMMANDS = {
            {"chromium", "--version"},
            {"chromium-browser", "--version"},
            {"google-chrome", "--version"}
    };
    private static final String PAYMENT_DETAIL_PATH = "/payment/detail";
    private static final String PAYMENT_ADMIN_LIST_PATH = "/payment/admin/list";
    private static final String EXPECTED_PAYMENT_DETAIL_TITLE = "Payment Detail";
    private static final String EXPECTED_PAYMENT_DETAIL_HEADER = "Payment Detail";
    private static final String EXPECTED_PAYMENT_LIST_TITLE = "Payment List";
    private static final String EXPECTED_PAYMENT_ADMIN_DETAIL_TITLE = "Payment Admin Detail";
    private static final By PAGE_HEADER = By.tagName("h2");
    private static final By ACCEPT_PAYMENT_BUTTON = By.id("acceptPaymentButton");
    private static final By PAYMENT_STATUS = By.id("paymentStatus");

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    @Autowired
    private PaymentService paymentService;

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
    void paymentDetailFormPage_titleIsCorrect(ChromeDriver driver) {
        driver.get(buildBaseUrl() + PAYMENT_DETAIL_PATH);
        assertEquals(EXPECTED_PAYMENT_DETAIL_TITLE, driver.getTitle());
    }

    @Test
    void paymentDetailFormPage_headerIsCorrect(ChromeDriver driver) {
        driver.get(buildBaseUrl() + PAYMENT_DETAIL_PATH);
        assertEquals(EXPECTED_PAYMENT_DETAIL_HEADER, driver.findElement(PAGE_HEADER).getText());
    }

    @Test
    void paymentDetailPageById_titleIsCorrect(ChromeDriver driver) {
        Payment payment = createBankTransferPayment();

        driver.get(buildBaseUrl() + "/payment/detail/" + payment.getId());
        assertEquals(EXPECTED_PAYMENT_DETAIL_TITLE, driver.getTitle());
    }

    @Test
    void paymentAdminListPage_titleIsCorrect(ChromeDriver driver) {
        driver.get(buildBaseUrl() + PAYMENT_ADMIN_LIST_PATH);
        assertEquals(EXPECTED_PAYMENT_LIST_TITLE, driver.getTitle());
    }

    @Test
    void paymentAdminDetailPageById_titleIsCorrect(ChromeDriver driver) {
        Payment payment = createBankTransferPayment();

        driver.get(buildBaseUrl() + "/payment/admin/detail/" + payment.getId());
        assertEquals(EXPECTED_PAYMENT_ADMIN_DETAIL_TITLE, driver.getTitle());
    }

    @Test
    void paymentAdminAcceptButton_updatesPaymentStatusToSuccess(ChromeDriver driver) {
        Payment payment = createBankTransferPayment();

        driver.get(buildBaseUrl() + "/payment/admin/detail/" + payment.getId());
        driver.findElement(ACCEPT_PAYMENT_BUTTON).click();

        assertEquals("SUCCESS", driver.findElement(PAYMENT_STATUS).getText());
    }

    private Payment createBankTransferPayment() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("bankName", "BCA");
        paymentData.put("referenceCode", "VA-" + UUID.randomUUID());

        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName("Product-" + UUID.randomUUID());
        product.setProductQuantity(1);

        Order order = new Order(
                UUID.randomUUID().toString(),
                java.util.List.of(product),
                Instant.now().getEpochSecond(),
                "Payment Functional Tester"
        );

        return paymentService.addPayment(order, "BANK_TRANSFER", paymentData);
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
