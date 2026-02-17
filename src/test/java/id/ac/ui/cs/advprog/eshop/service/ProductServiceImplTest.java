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
        Product product = buildProduct("id-1", "Tea", 5);
        Mockito.when(productRepository.create(product)).thenReturn(product);

        Product created = productService.create(product);

        assertSame(product, created);
        Mockito.verify(productRepository).create(product);
    }

    @Test
    void findAllReturnsAllProductsFromIterator() {
        Product product1 = buildProduct("id-1", "Tea", 5);
        Product product2 = buildProduct("id-2", "Coffee", 7);
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
        Mockito.when(productRepository.delete("id-1")).thenReturn(true);

        boolean deleted = productService.delete("id-1");

        assertTrue(deleted);
        Mockito.verify(productRepository).delete("id-1");
    }

    @Test
    void deleteReturnsFalseWhenRepositoryDoesNotDeleteData() {
        Mockito.when(productRepository.delete("missing")).thenReturn(false);

        boolean deleted = productService.delete("missing");

        assertFalse(deleted);
        Mockito.verify(productRepository).delete("missing");
    }

    @Test
    void findByIdReturnsProductWhenRepositoryFindsData() {
        Product product = buildProduct("id-1", "Tea", 5);
        Mockito.when(productRepository.findById("id-1")).thenReturn(product);

        Product found = productService.findById("id-1");

        assertSame(product, found);
        Mockito.verify(productRepository).findById("id-1");
    }

    @Test
    void findByIdReturnsNullWhenRepositoryReturnsNull() {
        Mockito.when(productRepository.findById("missing")).thenReturn(null);

        Product found = productService.findById("missing");

        assertNull(found);
        Mockito.verify(productRepository).findById("missing");
    }

    @Test
    void updateReturnsTrueWhenRepositoryUpdatesData() {
        Product product = buildProduct("id-1", "Tea", 10);
        Mockito.when(productRepository.update(product)).thenReturn(true);

        boolean updated = productService.update(product);

        assertTrue(updated);
        Mockito.verify(productRepository).update(product);
    }

    @Test
    void updateReturnsFalseWhenRepositoryRejectsUpdate() {
        Product product = buildProduct("missing", "Tea", 10);
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
