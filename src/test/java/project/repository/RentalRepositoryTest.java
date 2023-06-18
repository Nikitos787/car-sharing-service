package project.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import project.model.Rental;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RentalRepositoryTest {
    private static final int EXPECTED_SIZE = 1;
    private static final int NOT_EXPECTED_SIZE = 2;
    private static final Long EXPECTED_ID = 101L;
    private static final Long NOT_EXPECTED_ID = 2L;
    private static final int POSITION = 0;
    private static final boolean ACTIVE = true;
    private static final boolean NOT_ACTIVE = false;
    private static final int YEAR = 2023;
    private static final int MONTH = 1;
    private static final int NOT_EXPECTED_MONTH = 2;
    private static final int DAY_OF_MONTH = 1;
    private static final int RETURN_DAY_OF_MONTH = 2;
    private static final int HOUR = 0;
    private static final int MINUTES = 30;
    private static final int SECONDS = 31;
    private static final int SIZE_OF_OVERDUE_RENTAL = 0;
    private static final int OVERDUE_YEAR = 2022;
    private static final int OVERDUE_TEST_DIGIT = 2;

    @Container
    static MySQLContainer<?> database = new MySQLContainer<>("mysql:5.7")
            .withDatabaseName("springboot")
            .withPassword("springboot")
            .withUsername("springboot");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.datasource.url", database::getJdbcUrl);
        propertyRegistry.add("spring.datasource.password", database::getPassword);
        propertyRegistry.add("spring.datasource.username", database::getUsername);
    }

    @Autowired
    private RentalRepository rentalRepository;

    @Test
    @Sql("/scripts/init_rentals.sql")
    void findByUserIdAndIsActiveTrue_ok() {
        List<Rental> actual = rentalRepository.findByIdAndIsActive(EXPECTED_ID, ACTIVE);
        assertEquals(EXPECTED_SIZE, actual.size());
        assertEquals(EXPECTED_ID, actual.get(POSITION).getUser().getId());
        assertEquals(EXPECTED_ID, actual.get(POSITION).getCar().getId());
        assertNull(actual.get(POSITION).getActualDate());
        assertEquals(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR,
                MINUTES, SECONDS), actual.get(POSITION).getRentalDate());
        assertEquals(LocalDateTime.of(YEAR, MONTH, RETURN_DAY_OF_MONTH, HOUR,
                MINUTES, SECONDS), actual.get(POSITION).getReturnDate());

    }

    @Test
    @Sql("/scripts/init_rentals.sql")
    void findByUserIdAndIsActiveTrue_notOk() {
        List<Rental> actual = rentalRepository.findByIdAndIsActive(EXPECTED_ID, ACTIVE);
        assertNotEquals(NOT_EXPECTED_SIZE, actual.size());
        assertNotEquals(NOT_EXPECTED_ID, actual.get(POSITION).getUser().getId());
        assertNotEquals(NOT_EXPECTED_ID, actual.get(POSITION).getCar().getId());
        assertNull(actual.get(POSITION).getActualDate());
        assertNotEquals(LocalDateTime.of(YEAR, NOT_EXPECTED_MONTH, DAY_OF_MONTH, HOUR,
                MINUTES, SECONDS), actual.get(POSITION).getRentalDate());
        assertNotEquals(LocalDateTime.of(YEAR, NOT_EXPECTED_MONTH, RETURN_DAY_OF_MONTH, HOUR,
                MINUTES, SECONDS), actual.get(POSITION).getReturnDate());

    }

    @Test
    @Sql("/scripts/init_rentals.sql")
    void findByUserIdAndIsActiveFalse_Ok() {
        List<Rental> actual = rentalRepository.findByIdAndIsActive(EXPECTED_ID, NOT_ACTIVE);
        assertEquals(EXPECTED_SIZE, actual.size());
        assertEquals(EXPECTED_ID, actual.get(POSITION).getUser().getId());
        assertEquals(EXPECTED_ID, actual.get(POSITION).getCar().getId());
        assertNotNull(actual.get(POSITION).getActualDate());
        assertEquals(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR,
                MINUTES, SECONDS), actual.get(POSITION).getRentalDate());
        assertEquals(LocalDateTime.of(YEAR, MONTH, RETURN_DAY_OF_MONTH, HOUR,
                MINUTES, SECONDS), actual.get(POSITION).getReturnDate());

    }

    @Test
    @Sql("/scripts/init_rentals.sql")
    void findOverdueRental_ok() {
        LocalDateTime now = LocalDateTime.now();
        List<Rental> actual = rentalRepository.findOverdueRental(now);
        assertEquals(EXPECTED_SIZE, actual.size());
        assertNull(actual.get(POSITION).getActualDate());
    }

    @Test
    @Sql("/scripts/init_rentals.sql")
    void findOverdueRental_notOk() {
        LocalDateTime now = LocalDateTime.now();
        List<Rental> actual = rentalRepository.findOverdueRental(now);
        assertNotEquals(NOT_EXPECTED_SIZE, actual.size());
        assertNotNull(actual.get(POSITION).getCar());
    }

    @Test
    @Sql("/scripts/init_rentals.sql")
    void didNotFindOverdueRental_ok() {
        LocalDateTime now = LocalDateTime.of(OVERDUE_YEAR, OVERDUE_TEST_DIGIT, OVERDUE_TEST_DIGIT,
                OVERDUE_TEST_DIGIT, OVERDUE_TEST_DIGIT, OVERDUE_TEST_DIGIT);
        List<Rental> actual = rentalRepository.findOverdueRental(now);
        assertEquals(SIZE_OF_OVERDUE_RENTAL, actual.size());
    }
}
