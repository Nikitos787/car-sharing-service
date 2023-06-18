package project.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import project.model.user.RoleName;

class JwtTokenProviderTest {
    private static final String EMAIL = "bob@i.ua";
    private static final String PASSWORD = "12345678";
    private static final long VALIDITY_IN_MILLISECONDS = 3600000L;
    private static final String SECRET = "secret";

    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    private UserDetailsService userDetailsService;
    private String token;
    private List<String> roleList;


    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        userDetailsService = Mockito.mock(UserDetailsService.class);

        jwtTokenProvider = new JwtTokenProvider(userDetailsService);

        Field secretKey = JwtTokenProvider.class.getDeclaredField("secretKey");
        secretKey.setAccessible(true);
        secretKey.set(jwtTokenProvider, SECRET);

        Field validityInMilliseconds = JwtTokenProvider.class.
                getDeclaredField("validityInMilliSeconds");
        validityInMilliseconds.setAccessible(true);
        validityInMilliseconds.setLong(jwtTokenProvider, VALIDITY_IN_MILLISECONDS);

        roleList = new ArrayList<>();
        roleList.add(RoleName.MANAGER.name());
        roleList.add(RoleName.CUSTOMER.name());

        token = jwtTokenProvider.createToken(EMAIL, roleList);
    }

    @Test
    void createToken_ok() {
        assertNotNull(token);
        String actual = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        assertEquals(actual, EMAIL);
    }

    @Test
    void getUsername_ok() {
        String actual = jwtTokenProvider.getUsername(token);
        assertEquals(actual, EMAIL);
    }

    @Test
    void getAuthentication_ok() {
        UserDetails userDetails = User.withUsername(EMAIL)
                .password(PASSWORD)
                .roles(RoleName.CUSTOMER.name())
                .build();
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
        Authentication actual = jwtTokenProvider.getAuthentication(token);
        assertNotNull(actual);
        assertTrue(actual.getPrincipal().toString().contains(EMAIL));
    }

    @Test
    void resolveToken_ok() {
        String bearerToken = "Bearer " + token;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", bearerToken);
        String actual = jwtTokenProvider.resolveToken(request);
        assertNotNull(actual);
        assertEquals(actual, token);
    }

    @Test
    void validateToken_ok() {
        boolean actual = jwtTokenProvider.validateToken(token);
        assertTrue(actual);
    }
}
