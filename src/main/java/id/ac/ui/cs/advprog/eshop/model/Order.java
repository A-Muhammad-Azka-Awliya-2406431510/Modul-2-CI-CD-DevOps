package id.ac.ui.cs.advprog.eshop.model;

import lombok.Builder;
import lombok.Getter;
import java.util.List;
import java.util.Set;

@Builder
@Getter
public class Order {
    private static final Set<String> VALID_STATUSES = Set.of(
            "WAITING_PAYMENT",
            "FAILED",
            "SUCCESS",
            "CANCELLED"
    );

    private final String id;
    private final List<Product> products;
    private final Long orderTime;
    private final String author;
    private String status;

    public Order(String id, List<Product> products, Long orderTime, String author) {
        validateProducts(products);
        this.id = id;
        this.products = products;
        this.orderTime = orderTime;
        this.author = author;
        this.status = "WAITING_PAYMENT";
    }

    public Order(String id, List<Product> products, Long orderTime, String author, String status) {
        this(id, products, orderTime, author);

        this.setStatus(status);
    }

    public void setStatus(String status) {
        validateStatus(status);
        this.status = status;
    }

    private void validateProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Products cannot be empty");
        }
    }

    private void validateStatus(String status) {
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Invalid status");
        }
    }
}