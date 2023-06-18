package project.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.model.Rental;
import project.model.car.Car;
import project.model.payment.Payment;
import project.service.CarService;
import project.service.RentalService;
import project.service.impl.PaymentCalculationServiceImpl;

@ExtendWith(MockitoExtension.class)
class PaymentCalculationServiceImplTest {
    private static final Long ID = 1L;
    private static final BigDecimal DAILY_FEE = BigDecimal.valueOf(100);
    private static final BigDecimal MONEY_TO_PAY = BigDecimal.valueOf(200);

    private Rental rental;
    private Car car;
    private Payment payment;

    @InjectMocks
    private PaymentCalculationServiceImpl paymentCalculationService;

    @Mock
    private RentalService rentalService;

    @Mock
    private CarService carService;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(ID);
        car.setDailyFee(DAILY_FEE);

        rental = new Rental();
        rental.setId(ID);
        rental.setRentalDate(LocalDateTime.now().minusDays(2));
        rental.setReturnDate(LocalDateTime.now());
        rental.setCar(car);

        payment = new Payment();
        payment.setRental(rental);

    }

    @Test
    void calculatePaymentAmount_shouldReturnCorrectAmount() {
        when(rentalService.findById(ID)).thenReturn(rental);
        when(carService.findById(ID)).thenReturn(car);
        BigDecimal paymentAmount = paymentCalculationService.calculatePaymentAmount(payment);

        assertEquals(MONEY_TO_PAY, paymentAmount);
    }

    @Test
    void calculateFineAmount_shouldReturnCorrectAmount() {
        rental.setReturnDate(LocalDateTime.now().minusDays(1));
        rental.setActualDate(LocalDateTime.now());

        BigDecimal fineAmount = paymentCalculationService.calculateFineAmount(rental);

        BigDecimal expectedAmount = BigDecimal.valueOf(1.2);
        assertEquals(expectedAmount, fineAmount);
    }
}
