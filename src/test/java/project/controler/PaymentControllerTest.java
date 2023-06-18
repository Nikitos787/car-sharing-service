package project.controler;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import com.stripe.model.checkout.Session;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import project.config.SecurityConfig;
import project.model.Rental;
import project.model.payment.Payment;
import project.model.payment.PaymentStatus;
import project.model.payment.PaymentType;
import project.model.user.Role;
import project.model.user.RoleName;
import project.model.user.User;
import project.service.PaymentCalculationService;
import project.service.PaymentService;
import project.service.RentalService;
import project.stripe.PaymentProvider;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private PaymentCalculationService paymentCalculationService;

    @MockBean
    private PaymentProvider paymentProvider;

    @MockBean
    private RentalService rentalService;

    @MockBean
    private Session sessionMock;


    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    /*@Test
    @WithMockUser(roles = {"MANAGER", "CUSTOMER"})
    public void shouldCreatePayment_ok() {
        Role customer = new Role(1L, RoleName.CUSTOMER);
        User user = new User(1L, "user@i.ua", "Nikita", "Salohub",
                "11111111", 121211L,  Set.of(customer));

        Car car = new Car(1L, "Model", "Brand", 5, BigDecimal.valueOf(100),
                CarType.SUV, false);
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDateTime.of(2023, 1, 1, 1,
                1, 1));
        rental.setReturnDate(LocalDateTime.of(2023, 1, 2, 1,
                1, 1));
        rental.setActualDate(LocalDateTime.of(2023, 1, 3, 1,
                1, 1));
        rental.setCar(car);
        rental.setUser(user);

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setRental(rental);
        payment.setType(PaymentType.PAYMENT);

        BigDecimal moneyToFine = BigDecimal.valueOf(100);
        BigDecimal moneyToPay = BigDecimal.valueOf(100);

        payment.setPaymentAmount(moneyToPay);

        when(paymentCalculationService.calculatePaymentAmount(payment))
                .thenReturn(moneyToPay);

        when(paymentCalculationService.calculateFineAmount(rental))
                .thenReturn(moneyToFine);


        when(rentalService.findById(anyLong())).thenReturn(rental);

        when(paymentProvider.createPaymentSession(Mockito.any(BigDecimal.class),
                Mockito.any(BigDecimal.class), Mockito.any(Payment.class))).thenReturn(sessionMock);

        when(sessionMock.getUrl()).thenReturn("expectedUrl");
        when(sessionMock.getId()).thenReturn("id");

        payment.setUrl(sessionMock.getUrl());
        payment.setSessionId(sessionMock.getId());


        when(paymentService.save(payment)).thenReturn(payment);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(new PaymentRequestDto(payment.getRental().getId(), payment.getType()))
                .when()
                .post("/payments")
                .then()
                .statusCode(200);
    }*/

    @Test
    public void testGetSucceed() {
        Long paymentId = 1L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setStatus(PaymentStatus.PAID);
        payment.setSessionId("exampleSessionId");
        payment.setUrl("exampleUrl");
        payment.setPaymentAmount(BigDecimal.TEN);
        payment.setType(PaymentType.PAYMENT);
        payment.setRental(new Rental());

        when(paymentService.getById(anyLong())).thenReturn(payment);
        when(paymentService.save(Mockito.any(Payment.class))).thenReturn(payment);


        RestAssuredMockMvc.when()
                .get("/payments/success/{id}", paymentId)
                .then()
                .statusCode(200);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void shouldReturnOwnPayments_ok() {
        Role role = new Role();
        role.setRoleName(RoleName.CUSTOMER);
        User user = new User();
        user.setRoles(Set.of(role));
        user.setEmail("user@i.ua");

        Rental rental = new Rental();
        rental.setId(1L);
        rental.setUser(user);

        List<Payment> payments = List.of(new Payment(1L, "some_session_url",
                "some_session_id", BigDecimal.TEN, rental, PaymentStatus.PENDING,
                PaymentType.PAYMENT),
                new Payment(2L, "some_session_url",
                        "some_session_id", BigDecimal.TEN, rental, PaymentStatus.PENDING,
                        PaymentType.PAYMENT),
                new Payment(3L, "some_session_url",
                        "some_session_id", BigDecimal.TEN, rental, PaymentStatus.PENDING,
                        PaymentType.PAYMENT));

        when(paymentService.findAll()).thenReturn(payments);


        RestAssuredMockMvc.when()
                .get("/payments/my-payments")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetCanceled() {
        Long paymentId = 1L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setStatus(PaymentStatus.PAID);
        payment.setSessionId("exampleSessionId");
        payment.setUrl("exampleUrl");
        payment.setPaymentAmount(BigDecimal.TEN);
        payment.setType(PaymentType.PAYMENT);
        payment.setRental(new Rental());

        when(paymentService.getById(anyLong())).thenReturn(payment);
        when(paymentService.save(Mockito.any(Payment.class))).thenReturn(payment);
        RestAssuredMockMvc.when()
                .get("/payments/cancel/{id}", paymentId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void shouldFindByUserId() {
        Role role = new Role();
        role.setRoleName(RoleName.CUSTOMER);
        User user = new User();
        user.setId(1L);
        user.setRoles(Set.of(role));
        user.setEmail("user@i.ua");

        Rental rental = new Rental();
        rental.setId(1L);
        rental.setUser(user);

        List<Payment> payments = List.of(new Payment(1L, "some_session_url",
                        "some_session_id", BigDecimal.TEN, rental, PaymentStatus.PENDING,
                        PaymentType.PAYMENT),
                new Payment(2L, "some_session_url",
                        "some_session_id", BigDecimal.TEN, rental, PaymentStatus.PENDING,
                        PaymentType.PAYMENT),
                new Payment(3L, "some_session_url",
                        "some_session_id", BigDecimal.TEN, rental, PaymentStatus.PENDING,
                        PaymentType.PAYMENT));

        when(paymentService.getByUserId(anyLong())).thenReturn(payments);

        RestAssuredMockMvc.given()
                .queryParam("userId", user.getId())
                .when()
                .get("/payments")
                .then()
                .statusCode(200);

    }
}
