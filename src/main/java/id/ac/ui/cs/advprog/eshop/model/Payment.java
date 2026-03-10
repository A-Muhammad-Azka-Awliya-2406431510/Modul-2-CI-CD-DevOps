package id.ac.ui.cs.advprog.eshop.model;

import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import lombok.Getter;

import java.util.Map;

@Getter
public class Payment {
    private final String id;
    private final String method;
    private String status;
    private final Map<String, String> paymentData;

    public Payment(String id, String method, Map<String, String> paymentData) {
        this.id = id;
        this.method = method;
        this.status = PaymentStatus.WAITING_PAYMENT.getValue();
        this.paymentData = paymentData;
    }

    public Payment(String id, String method, String status, Map<String, String> paymentData) {
        this(id, method, paymentData);
        this.setStatus(status);
    }

    public void setStatus(String status) {
        if (!PaymentStatus.contains(status)) {
            throw new IllegalArgumentException();
        }
        this.status = status;
    }
}
