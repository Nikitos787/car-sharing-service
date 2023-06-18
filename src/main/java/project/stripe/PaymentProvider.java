package project.stripe;

import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import project.model.payment.Payment;

public interface PaymentProvider {
    Session createPaymentSession(BigDecimal payment, BigDecimal fine, Payment paymentObject);
}
