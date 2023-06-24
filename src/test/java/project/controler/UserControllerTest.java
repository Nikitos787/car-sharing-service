package project.controler;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Set;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import project.config.SecurityConfig;
import project.dto.request.UserRequestDto;
import project.model.user.Role;
import project.model.user.RoleName;
import project.model.user.User;
import project.service.RoleService;
import project.service.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class UserControllerTest {
    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private Authentication authentication;

    @MockBean
    private UserDetails userDetails;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void updateRole_ok() {
        Role customer = new Role(1L, RoleName.CUSTOMER);
        Role manager = new Role(1L, RoleName.MANAGER);
        User userBeforeUpdate = new User(1L, "user@i.ua", "Nikita", "Salohub",
                "11111111", 121211L,  Set.of(customer));

        User userAfterUpdate = new User(1L, "user@i.ua", "Nikita", "Salohub",
                "11111111", 121211L,  Set.of(manager));
        when(userService.findById(1L)).thenReturn(userBeforeUpdate);
        when(roleService.findByRoleName(RoleName.MANAGER)).thenReturn(manager);
        when(userService.update(userBeforeUpdate)).thenReturn(userAfterUpdate);

        RestAssuredMockMvc.given()
                .queryParam("role", "MANAGER")
                .when()
                .put("/users/1/role")
                .then()
                .statusCode(200);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void shouldReturnMe_ok() {
        Role manager = new Role(1L, RoleName.MANAGER);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userService.findByEmail(anyString()))
                .thenReturn(new User(1L, "nikitosik@i.ua", "Nikita", "Salohub",
                        "11111111", 121211L, Set.of(manager)));

        RestAssuredMockMvc
                .when()
                .get("/users/me")
                .then()
                .statusCode(200);
    }


    @Test
    @WithMockUser(roles = {"MANAGER", "CUSTOMER"})
    public void updateInfo_ok() {
        Role customer = new Role(1L, RoleName.CUSTOMER);
        User userBeforeUpdate = new User(1L, "manager@i.ua", "Bob", "Bobson",
                "11111111", 121211L, Set.of(customer));

        User userAfterUpdate = new User(1L, "manager@i.ua", "Bob", "Bobsonyuk",
                "11111111", 121211L, Set.of(customer));

        when(userService.findByEmail(anyString()))
                .thenReturn(userBeforeUpdate);
        when(userService.update(Mockito.any(User.class)))
                .thenReturn(userAfterUpdate);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(new UserRequestDto(userAfterUpdate.getEmail(),
                        userAfterUpdate.getPassword(), userAfterUpdate.getFirstName(),
                        userAfterUpdate.getLastName()))
                .when()
                .put("/users/me")
                .then()
                .statusCode(200);
    }
}
