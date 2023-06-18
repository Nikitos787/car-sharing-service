package project.service;

import project.model.Rental;
import project.model.payment.Payment;

public interface NotificationService {

    void sendMessageAboutSuccessRent(Rental rental);

    void checkOverdueRentals();

    void sendMessageToAdministrators(String message);

    void sendMessageAboutPaymentToUser(Payment payment, String message);
}
