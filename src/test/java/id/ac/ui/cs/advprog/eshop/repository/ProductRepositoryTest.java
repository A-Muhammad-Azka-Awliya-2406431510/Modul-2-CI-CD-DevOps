package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductRepositoryTest {
    private static final String PRODUCT_ID = "eb558e9f-1c39-460e-8860-71af6af63bd6";
    private static final String PRODUCT_NAME = "Sampo Cap Bambang";
    private static final int PRODUCT_QUANTITY = 100;
    private static final String OTHER_PRODUCT_ID = "a0f9de46-90b1-437d-a0bf-d0821dde9096";
    private static final String OTHER_PRODUCT_NAME = "Sampo Cap Usep";
    private static final int OTHER_PRODUCT_QUANTITY = 50;

    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepository();
    }

    @Test
    void testCreateAndFind() {
        Product product = createAndSaveProduct(PRODUCT_ID, PRODUCT_NAME, PRODUCT_QUANTITY);

        Iterator<Product> productIterator = productRepository.findAll();
        assertTrue(productIterator.hasNext());
        Product savedProduct = productIterator.next();
        assertEquals(product.getProductId(), savedProduct.getProductId());
        assertEquals(product.getProductName(), savedProduct.getProductName());
        assertEquals(product.getProductQuantity(), savedProduct.getProductQuantity());
    }

    @Test
    void testFindAllIfEmpty() {
        Iterator<Product> productIterator = productRepository.findAll();
        assertFalse(productIterator.hasNext());
    }

    @Test
    void testFindAllIfMoreThanOneProduct() {
        Product product1 = createAndSaveProduct(PRODUCT_ID, PRODUCT_NAME, PRODUCT_QUANTITY);
        Product product2 = createAndSaveProduct(OTHER_PRODUCT_ID, OTHER_PRODUCT_NAME, OTHER_PRODUCT_QUANTITY);

        Iterator<Product> productIterator = productRepository.findAll();
        assertTrue(productIterator.hasNext());
        Product savedProduct = productIterator.next();
        assertEquals(product1.getProductId(), savedProduct.getProductId());
        savedProduct = productIterator.next();
        assertEquals(product2.getProductId(), savedProduct.getProductId());
        assertEquals(product2.getProductName(), savedProduct.getProductName());
        assertEquals(product2.getProductQuantity(), savedProduct.getProductQuantity());
        assertFalse(productIterator.hasNext());
    }

    @Test
    void testDeleteRemovesExistingProduct() {
        createAndSaveProduct(PRODUCT_ID, PRODUCT_NAME, PRODUCT_QUANTITY);

        boolean deleted = productRepository.delete(PRODUCT_ID);

        assertTrue(deleted);
        assertNull(productRepository.findById(PRODUCT_ID));
    }

    @Test
    void testDeleteReturnsFalseForMissingId() {
        createAndSaveProduct(PRODUCT_ID, PRODUCT_NAME, PRODUCT_QUANTITY);

        boolean deleted = productRepository.delete(OTHER_PRODUCT_ID);

        assertFalse(deleted);
        assertNotNull(productRepository.findById(PRODUCT_ID));
    }

    @Test
    void testDeleteReturnsFalseForBlankId() {
        createAndSaveProduct(PRODUCT_ID, PRODUCT_NAME, PRODUCT_QUANTITY);

        boolean deleted = productRepository.delete(" ");

        assertFalse(deleted);
        assertNotNull(productRepository.findById(PRODUCT_ID));
    }

    @Test
    void testUpdateEditsExistingProduct() {
        createAndSaveProduct(PRODUCT_ID, PRODUCT_NAME, PRODUCT_QUANTITY);
        Product updated = buildProduct(PRODUCT_ID, OTHER_PRODUCT_NAME, OTHER_PRODUCT_QUANTITY);

        boolean updatedResult = productRepository.update(updated);

        assertTrue(updatedResult);
        Product savedProduct = productRepository.findById(PRODUCT_ID);
        assertNotNull(savedProduct);
        assertEquals(OTHER_PRODUCT_NAME, savedProduct.getProductName());
        assertEquals(OTHER_PRODUCT_QUANTITY, savedProduct.getProductQuantity());
    }

    @Test
    void testUpdateReturnsFalseForNullProduct() {
        boolean updatedResult = productRepository.update(null);

        assertFalse(updatedResult);
    }

    @Test
    void testUpdateReturnsFalseForBlankId() {
        Product updated = buildProduct(" ", OTHER_PRODUCT_NAME, OTHER_PRODUCT_QUANTITY);

        boolean updatedResult = productRepository.update(updated);

        assertFalse(updatedResult);
    }

    @Test
    void testUpdateReturnsFalseWhenIdNotFound() {
        Product updated = buildProduct(OTHER_PRODUCT_ID, OTHER_PRODUCT_NAME, OTHER_PRODUCT_QUANTITY);

        boolean updatedResult = productRepository.update(updated);

        assertFalse(updatedResult);
    }

    private Product createAndSaveProduct(String id, String name, int quantity) {
        Product product = buildProduct(id, name, quantity);
        productRepository.create(product);
        return product;
    }

    private Product buildProduct(String id, String name, int quantity) {
        Product product = new Product();
        product.setProductId(id);
        product.setProductName(name);
        product.setProductQuantity(quantity);
        return product;
    }
}
