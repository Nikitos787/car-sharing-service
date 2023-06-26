package project.service;

import java.math.BigDecimal;
import project.model.payment.Payment;

public interface PaymentCalculationService {
    BigDecimal calculatePaymentAmount(Payment payment);

    BigDecimal calculateFineAmount(Payment payment);
}
