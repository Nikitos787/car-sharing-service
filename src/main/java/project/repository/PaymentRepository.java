package project.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.model.payment.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("FROM Payment p JOIN FETCH p.rental pr JOIN FETCH pr.user pru WHERE pru.id = :id")
    List<Payment> findPaymentsByRental_UserId(@Param("id") Long userId);
}
