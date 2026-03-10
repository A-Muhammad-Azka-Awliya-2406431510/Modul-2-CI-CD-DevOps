package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final String METHOD_VOUCHER_CODE = "VOUCHER_CODE";
    private static final String METHOD_CASH_ON_DELIVERY = "CASH_ON_DELIVERY";
    private static final String METHOD_BANK_TRANSFER = "BANK_TRANSFER";

    @Autowired
    private PaymentRepository paymentRepository;
    private final Map<String, Order> orderByPaymentId = new HashMap<>();

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        String paymentId = UUID.randomUUID().toString();
        String initialStatus = resolveInitialStatus(method, paymentData);
        Payment payment = new Payment(paymentId, method, initialStatus, paymentData);

        paymentRepository.save(payment);
        orderByPaymentId.put(payment.getId(), order);

        return payment;
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        payment.setStatus(status);
        paymentRepository.save(payment);

        Order relatedOrder = orderByPaymentId.get(payment.getId());
        if (relatedOrder != null) {
            if (PaymentStatus.SUCCESS.getValue().equals(status)) {
                relatedOrder.setStatus(OrderStatus.SUCCESS.getValue());
            } else if (PaymentStatus.REJECTED.getValue().equals(status)) {
                relatedOrder.setStatus(OrderStatus.FAILED.getValue());
            }
        }

        return payment;
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    private String resolveInitialStatus(String method, Map<String, String> paymentData) {
        if (METHOD_VOUCHER_CODE.equals(method)) {
            return isValidVoucherCode(paymentData) ? PaymentStatus.SUCCESS.getValue() : PaymentStatus.REJECTED.getValue();
        }

        if (METHOD_CASH_ON_DELIVERY.equals(method)) {
            return hasNonBlankValue(paymentData, "address") && hasNonBlankValue(paymentData, "deliveryFee")
                    ? PaymentStatus.WAITING_PAYMENT.getValue()
                    : PaymentStatus.REJECTED.getValue();
        }

        if (METHOD_BANK_TRANSFER.equals(method)) {
            return hasNonBlankValue(paymentData, "bankName") && hasNonBlankValue(paymentData, "referenceCode")
                    ? PaymentStatus.WAITING_PAYMENT.getValue()
                    : PaymentStatus.REJECTED.getValue();
        }

        return PaymentStatus.WAITING_PAYMENT.getValue();
    }

    private boolean hasNonBlankValue(Map<String, String> paymentData, String key) {
        if (paymentData == null) {
            return false;
        }

        String value = paymentData.get(key);
        return value != null && !value.isBlank();
    }

    private boolean isValidVoucherCode(Map<String, String> paymentData) {
        if (paymentData == null) {
            return false;
        }

        String voucherCode = paymentData.get("voucherCode");
        if (voucherCode == null || voucherCode.length() != 16 || !voucherCode.startsWith("ESHOP")) {
            return false;
        }

        int numericCount = 0;
        for (char currentChar : voucherCode.toCharArray()) {
            if (Character.isDigit(currentChar)) {
                numericCount++;
            }
        }

        return numericCount == 8;
    }
}
