package id.ac.ui.cs.advprog.eshop.model;

import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentTest {
    private static final String PAYMENT_ID = "payment-001";
    private static final String METHOD = "VOUCHER_CODE";

    private Map<String, String> paymentData;

    @BeforeEach
    void setUp() {
        paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");
    }

    @Test
    void testCreatePaymentDefaultStatus() {
        Payment payment = new Payment(PAYMENT_ID, METHOD, paymentData);

        assertEquals(PAYMENT_ID, payment.getId());
        assertEquals(METHOD, payment.getMethod());
        assertSame(paymentData, payment.getPaymentData());
        assertEquals(PaymentStatus.WAITING_PAYMENT.getValue(), payment.getStatus());
    }

    @Test
    void testCreatePaymentWithSuccessStatus() {
        Payment payment = new Payment(PAYMENT_ID, METHOD, PaymentStatus.SUCCESS.getValue(), paymentData);

        assertEquals(PaymentStatus.SUCCESS.getValue(), payment.getStatus());
    }

    @Test
    void testCreatePaymentWithInvalidStatus() {
        assertThrows(IllegalArgumentException.class, () ->
                new Payment(PAYMENT_ID, METHOD, "MEOW", paymentData));
    }

    @Test
    void testSetStatusToRejected() {
        Payment payment = new Payment(PAYMENT_ID, METHOD, paymentData);

        payment.setStatus(PaymentStatus.REJECTED.getValue());

        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
    }

    @Test
    void testSetStatusToInvalidStatus() {
        Payment payment = new Payment(PAYMENT_ID, METHOD, paymentData);

        assertThrows(IllegalArgumentException.class, () -> payment.setStatus("MEOW"));
    }
}
