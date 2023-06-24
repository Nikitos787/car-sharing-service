package project.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.model.Rental;
import project.model.car.Car;
import project.model.car.CarType;
import project.repository.RentalRepository;
import project.service.CarService;
import project.service.impl.RentalServiceImpl;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {
    private static final Long EXPECTED_ID = 201L;
    private static final int VALID_AMOUNT_FOR_RENT = 2;
    private static final int AMOUNT_AFTER_CREATE_RENT = 1;
    private static final int NOT_VALID_AMOUNT_FOR_RENT = 0;
    private Rental rental;
    private Car car;
    @InjectMocks
    private RentalServiceImpl rentalService;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CarService carService;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(EXPECTED_ID);
        car.setDeleted(false);
        car.setCarType(CarType.SUV);
        car.setBrand("Brand");
        car.setModel("Model");
        car.setDailyFee(BigDecimal.TEN);

        rental = new Rental();
        rental.setId(EXPECTED_ID);
        rental.setCar(car);
    }

    @Test
    void save_ValidRental_ReturnsSavedRental_ok() {
        car.setInventory(VALID_AMOUNT_FOR_RENT);
        when(carService.findById(EXPECTED_ID)).thenReturn(car);
        when(rentalRepository.save(rental)).thenReturn(rental);
        Rental savedRental = rentalService.save(rental);
        assertNotNull(savedRental);
        assertEquals(rental.getId(), savedRental.getId());
        assertEquals(AMOUNT_AFTER_CREATE_RENT, car.getInventory());
    }

    @Test
    void save_NotValidRental_ThrowsException() {
        car.setInventory(NOT_VALID_AMOUNT_FOR_RENT);
        when(carService.findById(EXPECTED_ID)).thenReturn(car);
        assertThrows(RuntimeException.class, () -> { rentalService.save(rental);
        }, "RuntimeException expected");
    }
}