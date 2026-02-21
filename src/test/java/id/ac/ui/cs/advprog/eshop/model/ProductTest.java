package id.ac.ui.cs.advprog.eshop.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    private static final String PRODUCT_ID = "eb558e9f-1c39-460e-8860-71af6af63bd6";
    private static final String PRODUCT_NAME = "Sampo Cap Bambang";
    private static final int PRODUCT_QUANTITY = 100;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setProductId(PRODUCT_ID);
        product.setProductName(PRODUCT_NAME);
        product.setProductQuantity(PRODUCT_QUANTITY);
    }

    @Test
    void testGetProductId() {
        assertEquals(PRODUCT_ID, product.getProductId());
    }

    @Test
    void testGetProductName() {
        assertEquals(PRODUCT_NAME, product.getProductName());
    }

    @Test
    void testGetProductQuantity() {
        assertEquals(PRODUCT_QUANTITY, product.getProductQuantity());
    }

    @Test
    void testDefaultValuesWhenObjectIsCreated() {
        Product newProduct = new Product();

        assertNull(newProduct.getProductId());
        assertNull(newProduct.getProductName());
        assertEquals(0, newProduct.getProductQuantity());
    }

    @Test
    void testSetProductIdToNull() {
        product.setProductId(null);

        assertNull(product.getProductId());
    }

    @Test
    void testSetProductNameToBlank() {
        product.setProductName(" ");

        assertEquals(" ", product.getProductName());
    }

    @Test
    void testSetProductNameToNull() {
        product.setProductName(null);

        assertNull(product.getProductName());
    }

    @Test
    void testSetProductQuantityToZero() {
        product.setProductQuantity(0);

        assertEquals(0, product.getProductQuantity());
    }

    @Test
    void testSetProductQuantityToNegativeValue() {
        product.setProductQuantity(-1);

        assertEquals(-1, product.getProductQuantity());
    }
}
