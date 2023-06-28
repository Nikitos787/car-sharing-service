package project.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import project.model.payment.Payment;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ContainersConfig.class)
class PaymentRepositoryTest {
    private static final int EXPECTED_SIZE = 1;
    private static final int NOT_EXPECTED_SIZE = 2;
    private static final Long EXPECTED_ID = 101L;
    private static final Long NOT_EXPECTED_ID = 2L;
    private static final int POSITION = 0;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @Sql("/scripts/init_payments.sql")
    void findPaymentByRentalUserId_ok() {
        List<Payment> actual = paymentRepository.findPaymentByRental_UserId(EXPECTED_ID);
        assertEquals(EXPECTED_SIZE, actual.size());
        assertEquals(EXPECTED_ID, actual.get(POSITION).getRental().getUser().getId());
    }

    @Test
    @Sql("/scripts/init_payments.sql")
    void findPaymentByRentalUserId_notOk() {
        List<Payment> actual = paymentRepository.findPaymentByRental_UserId(EXPECTED_ID);
        assertNotEquals(NOT_EXPECTED_SIZE, actual.size());
        assertNotEquals(NOT_EXPECTED_ID, actual.get(POSITION).getRental().getUser().getId());
    }

    @Test
    @Sql("/scripts/init_payments.sql")
    void findPaymentByRentalWrongUserId_notOk() {
        List<Payment> actual = paymentRepository.findPaymentByRental_UserId(NOT_EXPECTED_ID);
        assertEquals(0, actual.size());
        assertThrows(IndexOutOfBoundsException.class, () -> {
            actual.get(POSITION).getRental().getUser().getId();
        }, "IndexOutOfBoundsException expected");
    }
}
