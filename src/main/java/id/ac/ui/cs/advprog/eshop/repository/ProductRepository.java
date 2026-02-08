package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Repository
public class ProductRepository {
    private List<Product> productData = new ArrayList<>();

    /*
    public Product create(Product product) {
        productData.add(product);
        return product;
    }
     */

    public Product create(Product product) {
        if (product.getProductId() == null || product.getProductId().isBlank()) {
            product.setProductId(UUID.randomUUID().toString());
        }
        productData.add(product);
        return product;
    }

    public Iterator<Product> findAll() {
        return productData.iterator();
    }

    /*
    public Product findById(String id) {
        return productData.stream()
                .filter(p -> p.getProductId().equals(id))
                .findFirst()
                .orElse(null);
    }
     */

    public Product findById(String id) {
        if (id == null || id.isBlank()) return null;

        return productData.stream()
                .filter(p -> id.equals(p.getProductId()))
                .findFirst()
                .orElse(null);
    }


    public boolean update(Product updated) {
        if (!isValidForUpdate(updated)) {
            return false;
        }

        return updateIfExists(updated);
    }

    private boolean isValidForUpdate(Product product) {
        return product != null
                && product.getProductId() != null
                && !product.getProductId().isBlank();
    }

    private boolean updateIfExists(Product updated) {
        for (Product current : productData) {
            if (updated.getProductId().equals(current.getProductId())) {
                applyUpdates(current, updated);
                return true;
            }
        }
        return false;
    }

    private void applyUpdates(Product target, Product source) {
        target.setProductName(source.getProductName());
        target.setProductQuantity(source.getProductQuantity());
    }

}
