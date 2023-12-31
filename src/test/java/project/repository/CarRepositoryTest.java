package project.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import project.model.car.Car;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ContainersConfig.class)
class CarRepositoryTest {
    private static final int SIZE = 63;
    private static final int WRONG_SIZE = 65;
    private static final Long ID_FOR_DELETE = 101L;
    private static final Long ID_ALREADY_DELETED_CAR = 102L;

    @Autowired
    private CarRepository carRepository;

    @Test
    @Sql("/scripts/init_cars.sql")
    public void shouldDelete_ok() {
        carRepository.safeDelete(ID_FOR_DELETE);
        Optional<Car> actual = carRepository.findByIdAndDeletedFalse(ID_FOR_DELETE);
        assertFalse(actual.isPresent());
    }

    @Test
    @Sql("/scripts/init_cars.sql")
    public void shouldFindAll_ok() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Car> actual = carRepository.findAll(pageable);
        Assertions.assertEquals(SIZE, actual.getTotalElements());
    }

    @Test
    @Sql("/scripts/init_cars.sql")
    public void shouldFindAll_notOk() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Car> actual = carRepository.findAll(pageable);
        assertNotEquals(WRONG_SIZE, actual.getTotalElements());
    }

    @Test
    @Sql("/scripts/init_cars.sql")
    public void shouldFindById_notOk() {
        Optional<Car> actual = carRepository.findByIdAndDeletedFalse(ID_ALREADY_DELETED_CAR);
        assertFalse(actual.isPresent());
    }
}
