package project.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.model.Rental;
import project.model.car.Car;
import project.model.payment.Payment;
import project.service.CarService;
import project.service.PaymentCalculationService;
import project.service.RentalService;

@Service
@RequiredArgsConstructor
public class PaymentCalculationServiceImpl implements PaymentCalculationService {
    private static final double FINE_MULTIPLIER = 1.2;

    private final RentalService rentalService;
    private final CarService carService;

    @Override
    public BigDecimal calculatePaymentAmount(Payment payment) {
        Rental rental = rentalService.findById(payment.getRental().getId());
        Car car = carService.findById(rental.getCar().getId());
        long daysRental = ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate());
        return car.getDailyFee().multiply(BigDecimal.valueOf(daysRental));
    }

    @Override
    public BigDecimal calculateFineAmount(Payment payment) {
        Rental rental = rentalService.findById(payment.getRental().getId());
        long daysActual = rental.getActualDate() != null
                ? ChronoUnit.DAYS.between(rental.getReturnDate(), rental.getActualDate()) : 0;
        BigDecimal dailyFee = rental.getCar().getDailyFee();
        return dailyFee.multiply(BigDecimal.valueOf((daysActual)
                * FINE_MULTIPLIER)).divide(BigDecimal.valueOf(100), RoundingMode.UP);
    }
}
