package project.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import project.model.user.Role;
import project.model.user.RoleName;
import project.model.user.User;
import project.service.UserService;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    private static final String EMAIL = "bob@i.ua";
    private static final String PASSWORD = "111111111";

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private UserService userService;
    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1L);
        role.setRoleName(RoleName.CUSTOMER);
        user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRoles(Set.of(role));
    }

    @Test
    void loadUserByUsername_Ok() {
        when(userService.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        UserDetails actual = customUserDetailsService.loadUserByUsername(EMAIL);
        assertNotNull(actual);
        assertEquals(EMAIL, actual.getUsername());
        assertEquals(PASSWORD, actual.getPassword());
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        when(userService.findByEmail(EMAIL)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(EMAIL);
        }, "UsernameNotFoundException expected");
    }
}
