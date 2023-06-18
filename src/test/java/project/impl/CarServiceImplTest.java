package project.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.model.car.Car;
import project.model.car.CarType;
import project.repository.CarRepository;
import project.service.impl.CarServiceImpl;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {
    private static final Long ID = 1L;
    private static final String BRAND = "BMW";
    private static final String MODEL = "X5";
    private static final int INVENTORY = 5;
    private static final BigDecimal DAILY_FEE = BigDecimal.valueOf(100);

    private Car expectedCar;
    @InjectMocks
    private CarServiceImpl carService;

    @Mock
    private CarRepository carRepository;


    @BeforeEach
    void setUp() {
        expectedCar = new Car();
        expectedCar.setId(ID);
        expectedCar.setBrand(BRAND);
        expectedCar.setDailyFee(DAILY_FEE);
        expectedCar.setCarType(CarType.UNIVERSAL);
        expectedCar.setModel(MODEL);
        expectedCar.setInventory(INVENTORY);
        expectedCar.setDeleted(false);
    }

    @Test
    public void removeCarFromInventory_ok() {
        when(carRepository.save(expectedCar)).thenReturn(expectedCar);
        when(carRepository.findByIdAndDeletedFalse(ID)).thenReturn(Optional.of(expectedCar));
        Car car = carService.removeCarFromInventory(ID);
        assertEquals(INVENTORY - 1, car.getInventory());
    }

    @Test
    public void removeCarFromInventory_invalidInventory() {
        expectedCar.setInventory(0);
        when(carRepository.findByIdAndDeletedFalse(ID)).thenReturn(Optional.of(expectedCar));
        assertThrows(RuntimeException.class, () -> carService.removeCarFromInventory(ID));
    }
}
