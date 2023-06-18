package project.controler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import project.dto.request.RentalRequestDto;
import project.model.Rental;
import project.model.car.Car;
import project.model.car.CarType;
import project.model.user.Role;
import project.model.user.RoleName;
import project.model.user.User;
import project.service.CarService;
import project.service.NotificationService;
import project.service.RentalService;
import project.service.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalService rentalService;

    @MockBean
    private UserService userService;

    @MockBean
    private CarService carService;

    @MockBean
    private NotificationService notificationService;


    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "CUSTOMER"})
    public void shouldCreateRental_ok() {
        Role manager = new Role(1L, RoleName.MANAGER);
        User user = new User(1L, "nikitosik@i.ua", "Nikita", "Salohub",
                "11111111", 121211L, Set.of(manager));
        Car car = new Car(1L, "Model", "Brand", 5, BigDecimal.valueOf(100),
                CarType.SUV, false);

        Rental rental = new Rental(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now(), null, car, user);

        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(rentalService.save(any(Rental.class))).thenReturn(rental);
        when((carService.findById(anyLong()))).thenReturn(car);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(new RentalRequestDto(rental.getRentalDate(), rental.getReturnDate(),
                        rental.getCar().getId()))
                .when()
                .post("/rentals")
                .then()
                .statusCode(200);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void shouldReturnCar_ok() {
        Role manager = new Role(1L, RoleName.MANAGER);
        User user = new User(1L, "nikitosik@i.ua", "Nikita", "Salohub",
                "11111111", 121211L, Set.of(manager));
        Car afterReturnCar = new Car(1L, "Model", "Brand", 6, BigDecimal.valueOf(100),
                CarType.SUV, false);
        Rental afterReturnRental = new Rental(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), afterReturnCar, user);

        when(rentalService.returnRental(anyLong())).thenReturn(afterReturnRental);

        RestAssuredMockMvc.when()
                .put("/rentals/{id}/return", afterReturnCar.getId())
                .then()
                .statusCode(200);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void shouldGetRentalByUserIdAndIsAlive_ok() {
        Role manager = new Role(1L, RoleName.MANAGER);
        User user = new User(1L, "nikitosik@i.ua", "Nikita", "Salohub",
                "11111111", 121211L, Set.of(manager));
        Car car = new Car(1L, "Model", "Brand", 6, BigDecimal.valueOf(100),
                CarType.SUV, false);
        Rental rental = new Rental(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now(), null, car, user);

        when(rentalService.findByIdAndIsActive(anyLong(), anyBoolean()))
                .thenReturn(List.of(rental));

        RestAssuredMockMvc.given()
                .queryParam("userId", user.getId())
                .queryParam("status", true)
                .when()
                .get("/rentals")
                .then()
                .statusCode(200);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void shouldReturnRentalById_ok() {
        Role manager = new Role(1L, RoleName.MANAGER);
        User user = new User(1L, "nikitosik@i.ua", "Nikita", "Salohub",
                "11111111", 121211L, Set.of(manager));
        Car car = new Car(1L, "Model", "Brand", 6, BigDecimal.valueOf(100),
                CarType.SUV, false);
        Rental rental = new Rental(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now(), null, car, user);

        when(rentalService.findById(anyLong())).thenReturn(rental);

        RestAssuredMockMvc.when()
                .get("/rentals/{id}", rental.getId())
                .then()
                .statusCode(200);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void shouldReturnOwnRentals() {
        Role cutomer = new Role(1L, RoleName.CUSTOMER);
        User user = new User(1L, "nikitosik@i.ua", "Nikita", "Salohub",
                "11111111", 121211L, Set.of(cutomer));
        Car car = new Car(1L, "Model", "Brand", 6, BigDecimal.valueOf(100),
                CarType.SUV, false);
        Rental rental = new Rental(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now(), null, car, user);

        when(rentalService.findAll()).thenReturn(List.of(rental));

        RestAssuredMockMvc.
                when()
                .get("/rentals/my-rentals")
                .then()
                .statusCode(200);
    }
}
