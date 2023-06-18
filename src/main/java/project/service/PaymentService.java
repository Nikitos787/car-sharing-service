package project.service;

import java.util.List;
import project.model.payment.Payment;

public interface PaymentService {

    Payment save(Payment payment);

    void delete(Payment payment);

    Payment getById(Long id);

    List<Payment> getByUserId(Long userId);

    List<Payment> findAll();

}
