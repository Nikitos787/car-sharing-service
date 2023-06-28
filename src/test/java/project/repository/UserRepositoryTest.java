package project.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import project.model.user.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ContainersConfig.class)
class UserRepositoryTest {
    private static final String EMAIL = "user@i.ua";
    private static final String WRONG_EMAIL = "wrong@i.ua";
    private static final String EXCEPTION_MESSAGE = "Can't find user by email";
    private static final Long USER_ID = 3L;
    private static final String PASSWORD = "11111111";
    private static final String USER_FIRST_NAME = "User";

    @Autowired
    private UserRepository userRepository;

    @Test
    @Sql("/scripts/init_users.sql")
    void shouldReturnUserWithEmail_ok() {
        User actual = userRepository.findByEmail(EMAIL).orElseThrow(() ->
                new NoSuchElementException(EXCEPTION_MESSAGE));
        assertEquals(EMAIL, actual.getEmail());
        assertEquals(PASSWORD, actual.getPassword());
        assertEquals(USER_FIRST_NAME, actual.getFirstName());
    }

    @Test
    @Sql("/scripts/init_users.sql")
    void notFoundUserWithWrongEmail_notOk() {
        assertThrows(NoSuchElementException.class, () -> {
            userRepository.findByEmail(WRONG_EMAIL).orElseThrow();
        }, "NoSuchElementException expected");
    }

    @Test
    @Sql("/scripts/init_users.sql")
    void notFoundUserAfterDelete_notOk() {
        userRepository.deleteAll();
        assertThrows(NoSuchElementException.class, () -> {
            userRepository.findByEmail(EMAIL).orElseThrow();
        }, "NoSuchElementException expected");
    }
}
