package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @InjectMocks
    PaymentServiceImpl paymentService;

    @Spy
    PaymentRepository paymentRepository;

    private Order order;
    private Map<String, String> voucherPaymentData;

    @BeforeEach
    void setUp() {
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);
        products.add(product);

        order = new Order(
                "13652556-012a-4c07-b546-54eb1396d79b",
                products,
                1708560000L,
                "Safira Sudrajat"
        );

        voucherPaymentData = new HashMap<>();
        voucherPaymentData.put("voucherCode", "ESHOP1234ABC5678");
    }

    @Test
    void testAddPayment() {
        Payment result = paymentService.addPayment(
                order,
                "VOUCHER_CODE",
                voucherPaymentData
        );

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("VOUCHER_CODE", result.getMethod());
        assertSame(voucherPaymentData, result.getPaymentData());
        assertEquals(PaymentStatus.SUCCESS.getValue(), result.getStatus());
        assertEquals(OrderStatus.SUCCESS.getValue(), order.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testAddPaymentVoucherInvalidShouldRejectAndFailOrder() {
        Map<String, String> invalidVoucherPaymentData = new HashMap<>();
        invalidVoucherPaymentData.put("voucherCode", "ESHOP1234ABC567");

        Payment result = paymentService.addPayment(
                order,
                "VOUCHER_CODE",
                invalidVoucherPaymentData
        );

        assertEquals(PaymentStatus.REJECTED.getValue(), result.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }

    @Test
    void testAddPaymentBankTransferValidDataShouldWaitForConfirmation() {
        Map<String, String> bankTransferPaymentData = new HashMap<>();
        bankTransferPaymentData.put("bankName", "BCA");
        bankTransferPaymentData.put("referenceCode", "VA-123456");

        Payment result = paymentService.addPayment(
                order,
                "BANK_TRANSFER",
                bankTransferPaymentData
        );

        assertEquals(PaymentStatus.WAITING_PAYMENT.getValue(), result.getStatus());
        assertEquals(OrderStatus.WAITING_PAYMENT.getValue(), order.getStatus());
    }

    @Test
    void testAddPaymentBankTransferWithEmptyBankNameShouldRejectAndFailOrder() {
        Map<String, String> invalidBankTransferPaymentData = new HashMap<>();
        invalidBankTransferPaymentData.put("bankName", "");
        invalidBankTransferPaymentData.put("referenceCode", "VA-123456");

        Payment result = paymentService.addPayment(
                order,
                "BANK_TRANSFER",
                invalidBankTransferPaymentData
        );

        assertEquals(PaymentStatus.REJECTED.getValue(), result.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }

    @Test
    void testAddPaymentBankTransferWithWhitespaceDataShouldStillBeWaiting() {
        Map<String, String> bankTransferPaymentData = new HashMap<>();
        bankTransferPaymentData.put("bankName", " ");
        bankTransferPaymentData.put("referenceCode", " ");

        Payment result = paymentService.addPayment(
                order,
                "BANK_TRANSFER",
                bankTransferPaymentData
        );

        assertEquals(PaymentStatus.WAITING_PAYMENT.getValue(), result.getStatus());
        assertEquals(OrderStatus.WAITING_PAYMENT.getValue(), order.getStatus());
    }

    @Test
    void testSetStatusToSuccess() {
        Payment payment = paymentService.addPayment(
                order,
                "VOUCHER_CODE",
                voucherPaymentData
        );

        Payment result = paymentService.setStatus(
                payment,
                PaymentStatus.SUCCESS.getValue()
        );

        assertEquals(PaymentStatus.SUCCESS.getValue(), result.getStatus());
        assertEquals(OrderStatus.SUCCESS.getValue(), order.getStatus());
    }

    @Test
    void testSetStatusToRejected() {
        Payment payment = paymentService.addPayment(
                order,
                "VOUCHER_CODE",
                voucherPaymentData
        );

        Payment result = paymentService.setStatus(
                payment,
                PaymentStatus.REJECTED.getValue()
        );

        assertEquals(PaymentStatus.REJECTED.getValue(), result.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }

    @Test
    void testSetStatusToInvalidStatus() {
        Payment payment = new Payment("payment-001", "VOUCHER_CODE", voucherPaymentData);

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.setStatus(payment, "MEOW")
        );
    }

    @Test
    void testGetPaymentIfIdFound() {
        Payment payment = paymentService.addPayment(
                order,
                "VOUCHER_CODE",
                voucherPaymentData
        );

        Payment result = paymentService.getPayment(payment.getId());

        assertEquals(payment.getId(), result.getId());
        assertEquals(payment.getMethod(), result.getMethod());
        assertEquals(payment.getStatus(), result.getStatus());
    }

    @Test
    void testGetPaymentIfIdNotFound() {
        Payment result = paymentService.getPayment("payment-404");

        assertNull(result);
    }

    @Test
    void testGetAllPayments() {
        paymentService.addPayment(order, "VOUCHER_CODE", voucherPaymentData);

        Map<String, String> transferPaymentData = new HashMap<>();
        transferPaymentData.put("bankName", "BCA");
        transferPaymentData.put("referenceCode", "VA-123456");
        paymentService.addPayment(order, "BANK_TRANSFER", transferPaymentData);

        List<Payment> payments = paymentService.getAllPayments();

        assertEquals(2, payments.size());
    }
}
