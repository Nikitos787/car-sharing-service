package project.controler;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import project.dto.request.UserLoginRequestDto;
import project.dto.request.UserRegisterRequestDto;
import project.model.user.Role;
import project.model.user.RoleName;
import project.model.user.User;
import project.security.AuthenticationService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {
    private static final Long ID = 1L;
    private static final String EMAIL = "nikitos@gmail.com";
    private static final String PASSWORD = "11111111";
    private static final String FIRST_NAME = "Nikita";
    private static final String LAST_NAME = "Salohub";

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private MockMvc mockMvc;
    private Role role;
    private User user;


    @BeforeEach
    void setUp() {
        role = new Role();
        role.setRoleName(RoleName.CUSTOMER);
        role.setId(ID);

        user = new User();
        user.setEmail(EMAIL);
        user.setLastName(LAST_NAME);
        user.setPassword(PASSWORD);
        user.setId(ID);
        user.setFirstName(FIRST_NAME);
        user.setRoles(Set.of(role));

        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    public void shouldRegister_ok() {
        when(authenticationService.register(eq(user.getEmail()), eq(user.getPassword()),
                eq(user.getFirstName()), eq(user.getLastName()))).thenReturn(user)
                .thenReturn(user);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(new UserRegisterRequestDto(user.getEmail(), user.getPassword(),
                        user.getPassword(), user.getFirstName(), user.getLastName()))
                .when()
                .post("/register")
                .then()
                .statusCode(200);
    }

    @Test
    public void shouldRegister_notOk() {
        when(authenticationService.register(anyString(), anyString(),
                anyString(), anyString())).thenReturn(user);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(new UserRegisterRequestDto(user.getEmail(), user.getPassword(),
                        user.getPassword() + " ", user.getFirstName(),
                        user.getLastName() ))
                .when()
                .post("/register")
                .then()
                .statusCode(400);
    }

    @Test
    public void shouldLogin_ok() throws Exception {
        when(authenticationService.login(anyString(), anyString()))
                .thenReturn(user);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequestDto(user.getEmail(), user.getPassword()))
                .when()
                .post("/login")
                .then()
                .statusCode(200);
    }
}
