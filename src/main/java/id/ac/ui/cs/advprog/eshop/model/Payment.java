package id.ac.ui.cs.advprog.eshop.model;

import lombok.Getter;

import java.util.Map;

@Getter
public class Payment {
    private static final String WAITING_PAYMENT = "WAITING_PAYMENT";
    private static final String SUCCESS = "SUCCESS";
    private static final String REJECTED = "REJECTED";

    private final String id;
    private final String method;
    private String status;
    private final Map<String, String> paymentData;

    public Payment(String id, String method, Map<String, String> paymentData) {
        this.id = id;
        this.method = method;
        this.status = WAITING_PAYMENT;
        this.paymentData = paymentData;
    }

    public Payment(String id, String method, String status, Map<String, String> paymentData) {
        this(id, method, paymentData);
        this.setStatus(status);
    }

    public void setStatus(String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException();
        }
        this.status = status;
    }

    private boolean isValidStatus(String status) {
        return WAITING_PAYMENT.equals(status)
                || SUCCESS.equals(status)
                || REJECTED.equals(status);
    }
}
