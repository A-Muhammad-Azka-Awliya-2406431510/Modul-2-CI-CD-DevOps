package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRepositoryTest {
    PaymentRepository paymentRepository;
    List<Payment> payments;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();
        payments = new ArrayList<>();

        Map<String, String> paymentData1 = new HashMap<>();
        paymentData1.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment1 = new Payment("payment-001", "VOUCHER_CODE", paymentData1);
        payments.add(payment1);

        Map<String, String> paymentData2 = new HashMap<>();
        paymentData2.put("voucherCode", "ESHOP1234ABC5679");
        Payment payment2 = new Payment("payment-002", "VOUCHER_CODE", paymentData2);
        payments.add(payment2);

        Map<String, String> paymentData3 = new HashMap<>();
        paymentData3.put("voucherCode", "ESHOP1234ABC5680");
        Payment payment3 = new Payment("payment-003", "VOUCHER_CODE", paymentData3);
        payments.add(payment3);
    }

    @Test
    void testSaveCreate() {
        Payment payment = payments.get(1);
        Payment result = paymentRepository.save(payment);

        Payment findResult = paymentRepository.findById(payment.getId());

        assertEquals(payment.getId(), result.getId());
        assertEquals(payment.getId(), findResult.getId());
        assertEquals(payment.getMethod(), findResult.getMethod());
        assertEquals(payment.getStatus(), findResult.getStatus());
        assertSame(payment.getPaymentData(), findResult.getPaymentData());
    }

    @Test
    void testSaveUpdate() {
        Payment payment = payments.get(1);
        paymentRepository.save(payment);

        Map<String, String> updatedPaymentData = new HashMap<>();
        updatedPaymentData.put("voucherCode", "INVALID123456789");
        Payment updatedPayment = new Payment(
                payment.getId(),
                payment.getMethod(),
                PaymentStatus.REJECTED.getValue(),
                updatedPaymentData
        );

        Payment result = paymentRepository.save(updatedPayment);
        Payment findResult = paymentRepository.findById(payment.getId());

        assertEquals(payment.getId(), result.getId());
        assertEquals(payment.getId(), findResult.getId());
        assertEquals(PaymentStatus.REJECTED.getValue(), findResult.getStatus());
        assertEquals("INVALID123456789", findResult.getPaymentData().get("voucherCode"));
    }

    @Test
    void testFindByIdIfIdFound() {
        for (Payment payment : payments) {
            paymentRepository.save(payment);
        }

        Payment findResult = paymentRepository.findById(payments.get(1).getId());

        assertEquals(payments.get(1).getId(), findResult.getId());
        assertEquals(payments.get(1).getMethod(), findResult.getMethod());
        assertEquals(payments.get(1).getStatus(), findResult.getStatus());
    }

    @Test
    void testFindByIdIfIdNotFound() {
        for (Payment payment : payments) {
            paymentRepository.save(payment);
        }

        Payment findResult = paymentRepository.findById("payment-999");

        assertNull(findResult);
    }

    @Test
    void testFindAllIfRepositoryHasData() {
        for (Payment payment : payments) {
            paymentRepository.save(payment);
        }

        List<Payment> paymentList = paymentRepository.findAll();

        assertEquals(3, paymentList.size());
        assertEquals(payments.get(0).getId(), paymentList.get(0).getId());
        assertEquals(payments.get(1).getId(), paymentList.get(1).getId());
        assertEquals(payments.get(2).getId(), paymentList.get(2).getId());
    }
}
