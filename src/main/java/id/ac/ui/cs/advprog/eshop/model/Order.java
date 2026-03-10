package id.ac.ui.cs.advprog.eshop.model;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Builder
@Getter
public class Order {
    private final String id;
    private final List<Product> products;
    private final Long orderTime;
    private final String author;
    private String status;

    public Order(String id, List<Product> products, Long orderTime, String author) {
        validateProducts(products);
        this.id = id;
        this.orderTime = orderTime;
        this.author = author;
        this.status = OrderStatus.WAITING_PAYMENT.getValue();

        if (products.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            this.products = products;
        }
    }

    public Order(String id, List<Product> products, Long orderTime, String author, String status) {
        this(id, products, orderTime, author);
        this.setStatus(status);
    }

    public void setStatus(String status) {
        if (OrderStatus.contains(status)) {
            this.status = status;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void validateProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Products cannot be empty");
        }
    }

    private void validateStatus(String status) {
        if (!OrderStatus.contains(status)) {
            throw new IllegalArgumentException("Invalid status");
        }
    }
}
