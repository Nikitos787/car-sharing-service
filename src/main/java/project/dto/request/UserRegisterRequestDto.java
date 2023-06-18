package project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.lib.FieldsValueMatch;
import project.lib.ValidEmail;

@Data
@FieldsValueMatch(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "Passwords do not match!"
)
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequestDto {
    @NotBlank(message = "email can't be empty or null")
    @ValidEmail
    private String email;
    @NotBlank
    @Size(min = 8, message = "password can't be less than 8")
    private String password;
    private String repeatPassword;
    @NotBlank(message = "first name can't be empty or null")
    private String firstName;
    @NotBlank(message = "last name can't be empty or null")
    private String lastName;
}
