package project.stripe.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import project.model.payment.Payment;
import project.model.payment.PaymentType;
import project.service.PaymentService;
import project.stripe.PaymentProvider;

@Component
@RequiredArgsConstructor
public class PaymentProviderImpl implements PaymentProvider {
    private static final String USD = "usd";
    private static final Long QUANTITY = 1L;
    @Value("${stripe-secret}")
    private String secretKey;
    @Value("${stripe-domen}")
    private String domen;
    private final PaymentService paymentService;

    public Session createPaymentSession(BigDecimal payment, BigDecimal fine,
                                        Payment paymentObject) {
        Stripe.apiKey = secretKey;
        Payment savedPayment = paymentService.save(paymentObject);
        Long paymentId = savedPayment.getId();
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(domen + "/payments/success/" + paymentId)
                .setCancelUrl(domen + "/payments/cancel/" + paymentId)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(USD)
                                                .setUnitAmount((long) (payment.doubleValue() * 100))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData
                                                                .ProductData
                                                                .builder()
                                                                .setName(paymentObject.getType()
                                                                        .name())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                );
        if (fine.doubleValue() > 0) {
            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(QUANTITY)
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency(USD)
                                            .setUnitAmount((long) (fine.doubleValue() * 100))
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData
                                                            .ProductData.builder()
                                                            .setName(PaymentType.FINE.name())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build()
            );
        }
        SessionCreateParams params = paramsBuilder.build();
        try {
            return Session.create(params);
        } catch (StripeException e) {
            throw new RuntimeException(String
                    .format("Can`t create session with params: %s", params), e);
        }
    }
}
