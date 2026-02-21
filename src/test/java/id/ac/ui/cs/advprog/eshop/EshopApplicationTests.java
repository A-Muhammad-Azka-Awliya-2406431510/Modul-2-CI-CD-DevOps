package id.ac.ui.cs.advprog.eshop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class EshopApplicationTests {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertTrue(applicationContext.getBeanDefinitionCount() > 0);
    }

    @Test
    void canInstantiateApplicationClass() {
        EshopApplication application = new EshopApplication();

        assertNotNull(application);
    }

    @Test
    void mainRunsWithoutThrowing() {
        assertDoesNotThrow(() -> EshopApplication.main(new String[] {
                "--spring.main.web-application-type=none",
                "--spring.main.lazy-initialization=true",
                "--spring.main.register-shutdown-hook=false"
        }));
    }

}
