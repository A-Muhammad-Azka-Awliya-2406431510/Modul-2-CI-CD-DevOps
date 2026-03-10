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
    private static final String KEY_VOUCHER_CODE = "voucherCode";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_DELIVERY_FEE = "deliveryFee";
    private static final String KEY_BANK_NAME = "bankName";
    private static final String KEY_REFERENCE_CODE = "referenceCode";
    private static final String VOUCHER_PREFIX = "ESHOP";
    private static final int VOUCHER_LENGTH = 16;
    private static final int REQUIRED_VOUCHER_DIGIT_COUNT = 8;

    private final PaymentRepository paymentRepository;
    private final Map<String, Order> orderByPaymentId = new HashMap<>();

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        Payment payment = createPayment(method, paymentData);
        paymentRepository.save(payment);
        orderByPaymentId.put(payment.getId(), order);
        updateRelatedOrderStatus(payment.getId(), payment.getStatus());
        return payment;
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        payment.setStatus(status);
        paymentRepository.save(payment);
        updateRelatedOrderStatus(payment.getId(), status);
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

    private Payment createPayment(String method, Map<String, String> paymentData) {
        String paymentId = UUID.randomUUID().toString();
        String initialStatus = resolveInitialStatus(method, paymentData);
        return new Payment(paymentId, method, initialStatus, paymentData);
    }

    private void updateRelatedOrderStatus(String paymentId, String paymentStatus) {
        Order relatedOrder = orderByPaymentId.get(paymentId);
        if (relatedOrder == null) {
            return;
        }

        String orderStatus = mapOrderStatus(paymentStatus);
        if (orderStatus != null) {
            relatedOrder.setStatus(orderStatus);
        }
    }

    private String mapOrderStatus(String paymentStatus) {
        if (PaymentStatus.SUCCESS.getValue().equals(paymentStatus)) {
            return OrderStatus.SUCCESS.getValue();
        }
        if (PaymentStatus.REJECTED.getValue().equals(paymentStatus)) {
            return OrderStatus.FAILED.getValue();
        }
        return null;
    }

    private String resolveInitialStatus(String method, Map<String, String> paymentData) {
        return switch (method) {
            case METHOD_VOUCHER_CODE -> resolveVoucherStatus(paymentData);
            case METHOD_CASH_ON_DELIVERY -> resolveCashOnDeliveryStatus(paymentData);
            case METHOD_BANK_TRANSFER -> resolveBankTransferStatus(paymentData);
            default -> PaymentStatus.WAITING_PAYMENT.getValue();
        };
    }

    private String resolveVoucherStatus(Map<String, String> paymentData) {
        return isValidVoucherCode(paymentData)
                ? PaymentStatus.SUCCESS.getValue()
                : PaymentStatus.REJECTED.getValue();
    }

    private String resolveCashOnDeliveryStatus(Map<String, String> paymentData) {
        return isComplete(paymentData, KEY_ADDRESS, KEY_DELIVERY_FEE)
                ? PaymentStatus.WAITING_PAYMENT.getValue()
                : PaymentStatus.REJECTED.getValue();
    }

    private String resolveBankTransferStatus(Map<String, String> paymentData) {
        return isComplete(paymentData, KEY_BANK_NAME, KEY_REFERENCE_CODE)
                ? PaymentStatus.WAITING_PAYMENT.getValue()
                : PaymentStatus.REJECTED.getValue();
    }

    private boolean isComplete(Map<String, String> paymentData, String firstKey, String secondKey) {
        return hasNonBlankValue(paymentData, firstKey)
                && hasNonBlankValue(paymentData, secondKey);
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

        String voucherCode = paymentData.get(KEY_VOUCHER_CODE);
        if (!hasValidVoucherStructure(voucherCode)) {
            return false;
        }

        return countDigits(voucherCode) == REQUIRED_VOUCHER_DIGIT_COUNT;
    }

    private boolean hasValidVoucherStructure(String voucherCode) {
        return voucherCode != null
                && voucherCode.length() == VOUCHER_LENGTH
                && voucherCode.startsWith(VOUCHER_PREFIX);
    }

    private int countDigits(String text) {
        int numericCount = 0;
        for (char currentChar : text.toCharArray()) {
            if (Character.isDigit(currentChar)) {
                numericCount++;
            }
        }
        return numericCount;
    }
}
