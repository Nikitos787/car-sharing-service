package project.lib;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import project.dto.request.UserRegisterRequestDto;

class FieldsValueMatchValidatorTest {
    private static final String EMAIL = "bob@i.ua";
    private static final String PASSWORD = "11111111";
    private static final String REPEAT_PASSWORD = "11111111";
    private static final String WRONG_PASSWORD = "000000000000";
    private static FieldsValueMatchValidator fieldsValueMatchValidator;
    private static ConstraintValidatorContext constraintValidatorContext;
    private static FieldsValueMatch constraintAnnotation;
    private static UserRegisterRequestDto registrationDto;

    @BeforeAll
    static void beforeAll() {
        registrationDto = new UserRegisterRequestDto();
        constraintValidatorContext = Mockito.mock(ConstraintValidatorContext.class);
        fieldsValueMatchValidator = new FieldsValueMatchValidator();
        constraintAnnotation = Mockito.mock(FieldsValueMatch.class);
        Mockito.when(constraintAnnotation.field()).thenReturn("password");
        Mockito.when(constraintAnnotation.fieldMatch()).thenReturn("repeatPassword");
        fieldsValueMatchValidator.initialize(constraintAnnotation);
    }

    @BeforeEach
    void setUp() {
        registrationDto.setPassword(PASSWORD);
        registrationDto.setEmail(EMAIL);
        registrationDto.setRepeatPassword(REPEAT_PASSWORD);
    }

    @Test
    void isValid_ok() {
        boolean actual = fieldsValueMatchValidator.isValid(registrationDto, constraintValidatorContext);
        assertTrue(actual);
    }

    @Test
    void isValid_notOk() {
        registrationDto.setRepeatPassword(WRONG_PASSWORD);
        boolean actual = fieldsValueMatchValidator.isValid(registrationDto, constraintValidatorContext);
        assertFalse(actual);
    }
}
