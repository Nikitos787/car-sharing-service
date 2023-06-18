package project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequestDto {
    @NotBlank(message = "login can't be empty or null")
    private String login;
    @NotBlank(message = "password can't be empty or null")
    private String password;

}
