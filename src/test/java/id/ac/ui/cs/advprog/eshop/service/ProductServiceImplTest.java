package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceImplTest {
    private static final String ID_ONE = "id-1";
    private static final String ID_TWO = "id-2";
    private static final String TEA = "Tea";
    private static final String COFFEE = "Coffee";
    private static final String MISSING = "missing";

    private ProductRepository productRepository;
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        productService = new ProductServiceImpl();
        ReflectionTestUtils.setField(productService, "productRepository", productRepository);
    }

    @Test
    void createDelegatesToRepository() {
        Product product = buildProduct(ID_ONE, TEA, 5);
        Mockito.when(productRepository.create(product)).thenReturn(product);

        Product created = productService.create(product);

        assertSame(product, created);
        Mockito.verify(productRepository).create(product);
    }

    @Test
    void findAllReturnsAllProductsFromIterator() {
        Product product1 = buildProduct(ID_ONE, TEA, 5);
        Product product2 = buildProduct(ID_TWO, COFFEE, 7);
        Mockito.when(productRepository.findAll()).thenReturn(List.of(product1, product2).iterator());

        List<Product> products = productService.findAll();

        assertEquals(2, products.size());
        assertSame(product1, products.get(0));
        assertSame(product2, products.get(1));
        Mockito.verify(productRepository).findAll();
    }

    @Test
    void findAllReturnsEmptyListWhenRepositoryHasNoData() {
        Iterator<Product> emptyIterator = List.<Product>of().iterator();
        Mockito.when(productRepository.findAll()).thenReturn(emptyIterator);

        List<Product> products = productService.findAll();

        assertNotNull(products);
        assertTrue(products.isEmpty());
        Mockito.verify(productRepository).findAll();
    }

    @Test
    void deleteReturnsTrueWhenRepositoryDeletesData() {
        Mockito.when(productRepository.delete(ID_ONE)).thenReturn(true);

        boolean deleted = productService.delete(ID_ONE);

        assertTrue(deleted);
        Mockito.verify(productRepository).delete(ID_ONE);
    }

    @Test
    void deleteReturnsFalseWhenRepositoryDoesNotDeleteData() {
        Mockito.when(productRepository.delete(MISSING)).thenReturn(false);

        boolean deleted = productService.delete(MISSING);

        assertFalse(deleted);
        Mockito.verify(productRepository).delete(MISSING);
    }

    @Test
    void findByIdReturnsProductWhenRepositoryFindsData() {
        Product product = buildProduct(ID_ONE, TEA, 5);
        Mockito.when(productRepository.findById(ID_ONE)).thenReturn(product);

        Product found = productService.findById(ID_ONE);

        assertSame(product, found);
        Mockito.verify(productRepository).findById(ID_ONE);
    }

    @Test
    void findByIdReturnsNullWhenRepositoryReturnsNull() {
        Mockito.when(productRepository.findById(MISSING)).thenReturn(null);

        Product found = productService.findById(MISSING);

        assertNull(found);
        Mockito.verify(productRepository).findById(MISSING);
    }

    @Test
    void updateReturnsTrueWhenRepositoryUpdatesData() {
        Product product = buildProduct(ID_ONE, TEA, 10);
        Mockito.when(productRepository.update(product)).thenReturn(true);

        boolean updated = productService.update(product);

        assertTrue(updated);
        Mockito.verify(productRepository).update(product);
    }

    @Test
    void updateReturnsFalseWhenRepositoryRejectsUpdate() {
        Product product = buildProduct(MISSING, TEA, 10);
        Mockito.when(productRepository.update(product)).thenReturn(false);

        boolean updated = productService.update(product);

        assertFalse(updated);
        Mockito.verify(productRepository).update(product);
    }

    @Test
    void updateDelegatesNullInputWithoutThrowing() {
        Mockito.when(productRepository.update(null)).thenReturn(false);

        boolean updated = productService.update(null);

        assertFalse(updated);
        Mockito.verify(productRepository).update(null);
    }

    private Product buildProduct(String id, String name, int quantity) {
        Product product = new Product();
        product.setProductId(id);
        product.setProductName(name);
        product.setProductQuantity(quantity);
        return product;
    }
}
