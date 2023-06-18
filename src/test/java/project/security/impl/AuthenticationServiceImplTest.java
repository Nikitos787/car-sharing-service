package project.security.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.exception.AuthenticationException;
import project.model.user.Role;
import project.model.user.RoleName;
import project.model.user.User;
import project.security.AuthenticationService;
import project.service.RoleService;
import project.service.UserService;

class AuthenticationServiceImplTest {
    private static final Long ID = 1L;
    private static final String EMAIL = "bob@i.ua";
    private static final String PASSWORD = "11111111";
    private static final String FIRST_NAME = "Bob";
    private static final String LAST_NAME = "Bobson";
    private static AuthenticationService authenticationService;
    private static UserService userService;
    private static RoleService roleService;
    private static PasswordEncoder passwordEncoder;
    private User user;
    private Role role;

    @BeforeAll
    static void beforeAll() {
        userService = Mockito.mock(UserService.class);
        passwordEncoder = new BCryptPasswordEncoder();
        roleService = Mockito.mock(RoleService.class);
        authenticationService =
                new AuthenticationServiceImpl(userService, passwordEncoder, roleService);
    }

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setRoleName(RoleName.CUSTOMER);
        user = new User();
        user.setId(ID);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRoles(Set.of(role));
    }

    @Test
    void register_ok() {
        when(roleService.findByRoleName(RoleName.CUSTOMER))
                .thenReturn(role);
        when(userService.save(any())).thenReturn(user);

        User actual = authenticationService.register(EMAIL, PASSWORD, FIRST_NAME, LAST_NAME);
        assertNotNull(actual);
        assertEquals(ID, actual.getId());
        assertEquals(EMAIL, actual.getEmail());
        assertEquals(PASSWORD, actual.getPassword());
    }

    @Test
    void login_ok() {
        user.setPassword(passwordEncoder.encode(PASSWORD));
        when(userService.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        User actual = null;
        try {
            actual = authenticationService.login(EMAIL, PASSWORD);
        } catch (AuthenticationException e) {
            Assertions.fail();
        }
        assertEquals(EMAIL, actual.getEmail());
    }

    @Test
    void login_notOk() {
        user.setPassword(passwordEncoder.encode(PASSWORD));
        when(userService.findByEmail("ERROR")).thenReturn(Optional.of(user));
        assertThrows(AuthenticationException.class, () -> {
            authenticationService.login(EMAIL, PASSWORD);
        }, "AuthenticationException expected");
    }
}
