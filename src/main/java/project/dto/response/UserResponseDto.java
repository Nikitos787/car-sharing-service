package project.dto.response;

import java.util.Set;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Set<String> roles;
}
