package project.lib;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailValidatorTest {
    private static final String EMAIL = "bob@i.ua";
    @InjectMocks
    private EmailValidator emailValidator;
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;


    @Test
    void isValid_ok() {
        boolean actual = emailValidator.isValid(EMAIL, constraintValidatorContext);
        assertTrue(actual);
    }

    @Test
    void isValid_withoutSnail_notOk() {
        String email = "bobi.ua";
        boolean actual = emailValidator.isValid(email, constraintValidatorContext);
        assertFalse(actual);
    }

    @Test
    void isValid_withoutDot_notOk() {
        String email = "bob@iua";
        boolean actual = emailValidator.isValid(email, constraintValidatorContext);
        assertFalse(actual);
    }

    @Test
    void isValid_emptyFirstPart_notOk() {
        String email = "@i.ua";
        boolean actual = emailValidator.isValid(email, constraintValidatorContext);
        assertFalse(actual);
    }

    @Test
    void isValid_withoutTail_notOk() {
        String email = "bob@";
        boolean actual = emailValidator.isValid(email, constraintValidatorContext);
        assertFalse(actual);
    }

    @Test
    void isValid_Null_notOk() {
        boolean actual = emailValidator.isValid(null, constraintValidatorContext);
        assertFalse(actual);
    }
}
