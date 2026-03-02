package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import java.util.Iterator;

public interface ProductRepositoryPort {
    Product create(Product product);
    Iterator<Product> findAll();
    boolean delete(String id);
    Product findById(String id);
    boolean update(Product updated);
}