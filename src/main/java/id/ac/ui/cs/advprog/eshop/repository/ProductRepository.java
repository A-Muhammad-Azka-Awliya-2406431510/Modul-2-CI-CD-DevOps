package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
public class ProductRepository {
    private final List<Product> productData = new ArrayList<>();

    public Product create(Product product) {
        Objects.requireNonNull(product, "product must not be null");

        assignProductIdIfMissing(product);
        productData.add(product);

        return product;
    }

    public Iterator<Product> findAll() {
        return productData.iterator();
    }

    public boolean delete(String id) {
        return productData.removeIf(p -> p.getProductId().equals(id));
    }

    private void assignProductIdIfMissing(Product product) {
        if (hasProductId(product)) {
            return;
        }
        product.setProductId(generateProductId());
    }

    private boolean hasProductId(Product product) {
        String productId = product.getProductId();
        return productId != null && !productId.isBlank();
    }

    private String generateProductId() {
        return UUID.randomUUID().toString();
    }
}
