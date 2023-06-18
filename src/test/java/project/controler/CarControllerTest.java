package project.controler;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import project.dto.request.CarRequestDto;
import project.model.car.Car;
import project.model.car.CarType;
import project.service.CarService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void shouldReturnAllCars_ok() {
        List<Car> cars = List.of(
                new Car(1L, "Model", "Brand", 5, BigDecimal.valueOf(100),
                        CarType.SUV, false),
                new Car(2L, "Model1", "Brand1", 5, BigDecimal.valueOf(100),
                        CarType.SUV, false),
                new Car(1L, "Model3", "Brand3", 5, BigDecimal.valueOf(100),
                        CarType.SUV, false));
        Page<Car> carsPage = new PageImpl<>(cars);
        when(carService.findAll(any(Pageable.class))).thenReturn(carsPage);

        RestAssuredMockMvc.when()
                .get("/cars")
                .then()
                .statusCode(200)
                .body("size()", equalTo(3));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void shouldReturnByParams_ok() {
        Map<String, String> params = Map.of("inventory", "5");
        List<Car> cars = List.of(
                new Car(1L, "Model", "Brand", 5, BigDecimal.valueOf(100),
                        CarType.SUV, false),
                new Car(2L, "Model1", "Brand1", 5, BigDecimal.valueOf(100),
                        CarType.SUV, false),
                new Car(1L, "Model3", "Brand3", 5, BigDecimal.valueOf(100),
                        CarType.SUV, false));
        when(carService.findAllByParams(params)).thenReturn(cars);

        RestAssuredMockMvc.given()
                .queryParam("inventory", "5")
                .when()
                .get("/cars/by-params")
                .then()
                .statusCode(200)
                .body("size()", equalTo(3));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void shouldReturnCarById_ok() {
        Car car = new Car(1L, "Model", "Brand", 5, BigDecimal.valueOf(100),
                CarType.SUV, false);
        when(carService.findById(1L)).thenReturn(car);

        RestAssuredMockMvc.when()
                .get("/cars/{id}", car.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void shouldUpdateInfoAboutCar() {
        Car car = new Car(1L, "Model", "Brand", 5, BigDecimal.valueOf(100),
                CarType.SUV, false);
        when(carService.update(anyLong(), any(Car.class))).thenReturn(car);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(new CarRequestDto(car.getModel(), car.getBrand(), car.getDailyFee(),
                        car.getCarType(), car.getInventory()))
                .when()
                .put("/cars/{id}", car.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void shouldDeleteCar() {
        Long carId = 1L;
        RestAssuredMockMvc.when()
                        .delete("/cars/{id}", carId)
                                .then()
                                        .statusCode(200);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void shouldAddCarToInventory() {
        Car car = new Car(1L, "Model", "Brand", 5, BigDecimal.valueOf(100),
                CarType.SUV, false);

        when(carService.addCarToInventory(1L)).thenReturn(car);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .when()
                .post("/cars/add/{id}", 1)
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("model", equalTo("Model"))
                .body("brand", equalTo("Brand"))
                .body("inventory", equalTo(5));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void shouldRemoveCarToInventory() {
        Car car = new Car(1L, "Model", "Brand", 5, BigDecimal.valueOf(100),
                CarType.SUV, false);

        when(carService.removeCarFromInventory(1L)).thenReturn(car);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/cars/remove/{id}", 1)
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("model", equalTo("Model"))
                .body("brand", equalTo("Brand"))
                .body("inventory", equalTo(5));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void createCar_ok() {
        Car car = new Car();
        car.setModel("Model");
        car.setBrand("Brand");
        car.setCarType(CarType.HATCHBACK);
        car.setDailyFee(BigDecimal.valueOf(100));
        car.setInventory(5);
        car.setDeleted(false);

        when(carService.save(car)).thenReturn(new Car(1L, "Model", "Brand",
                5, BigDecimal.valueOf(100),
                CarType.SUV, false));

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(new CarRequestDto(car.getModel(), car.getBrand(), car.getDailyFee(),
                        car.getCarType(), car.getInventory()))
                .when()
                .post("/cars")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }
}
