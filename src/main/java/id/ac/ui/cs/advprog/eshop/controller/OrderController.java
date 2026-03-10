package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/order")
public class OrderController {
    private static final String REDIRECT_ORDER_HISTORY = "redirect:/order/history";

    private final OrderService orderService;
    private final ProductService productService;

    @Autowired
    public OrderController(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping("/create")
    public String createOrderPage(Model model) {
        model.addAttribute("products", productService.findAll());
        return "createOrder";
    }

    @PostMapping("/create")
    public String createOrderPost(
            @RequestParam String author,
            @RequestParam(required = false) List<String> productIds
    ) {
        List<Product> selectedProducts = getSelectedProducts(productIds);
        if (!selectedProducts.isEmpty()) {
            Order order = new Order(
                    UUID.randomUUID().toString(),
                    selectedProducts,
                    Instant.now().getEpochSecond(),
                    author
            );
            orderService.createOrder(order);
        }

        return REDIRECT_ORDER_HISTORY;
    }

    @GetMapping("/history")
    public String orderHistoryPage() {
        return "orderHistory";
    }

    @PostMapping("/history")
    public String orderHistoryPost(@RequestParam String author, Model model) {
        model.addAttribute("author", author);
        model.addAttribute("orders", orderService.findAllByAuthor(author));
        return "orderList";
    }

    private List<Product> getSelectedProducts(List<String> productIds) {
        List<Product> selectedProducts = new ArrayList<>();
        if (productIds == null) {
            return selectedProducts;
        }

        for (String productId : productIds) {
            Product product = productService.findById(productId);
            if (product != null) {
                selectedProducts.add(product);
            }
        }
        return selectedProducts;
    }
}
