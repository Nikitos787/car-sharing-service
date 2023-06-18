package project.controler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.dto.request.UserLoginRequestDto;
import project.dto.request.UserRegisterRequestDto;
import project.dto.response.UserResponseDto;
import project.exception.AuthenticationException;
import project.model.user.User;
import project.security.AuthenticationService;
import project.security.jwt.JwtTokenProvider;
import project.service.NotificationService;
import project.service.mapper.ResponseDtoMapper;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseDtoMapper<UserResponseDto, User> responseDtoMapper;
    private final NotificationService notificationService;

    @PostMapping("/register")
    @Operation(summary = "endpoint for registration new user",
            description = "Data for registration. Be attentive that password and "
                    + "repeatPassword should be the same")
    public UserResponseDto register(
            @Parameter(schema = @Schema(type = "String",
                    defaultValue = "{\n"
                            + "\"email\":\"your@gmail.com\",\n"
                            + "\"password\":\"12345678\",\n"
                            + "\"repeatPassword\":\"12345678\",\n"
                            + "\"firstName\":\"YourName\",\n"
                            + "\"lastName\":\"YourLastName\" \n"
                            + "}"))
            @RequestBody @Valid UserRegisterRequestDto userRegisterDto) {
        User user = authenticationService.register(userRegisterDto.getEmail(),
                userRegisterDto.getPassword(),
                userRegisterDto.getFirstName(),
                userRegisterDto.getLastName());
        notificationService.sendMessageToAdministrators("New user was registered");
        return responseDtoMapper.mapToDto(user);
    }

    @PostMapping("/login")
    @Operation(summary = "endpoint for login as an user",
            description = "here you can send your login and password ")
    public ResponseEntity<Object> login(
            @Parameter(schema = @Schema(type = "String",
                    defaultValue = "{\n"
                            + "\"login\":\"your login\",\n"
                            + "\"password\":\"your password\"\n"
                            + "}"))
            @RequestBody @Valid UserLoginRequestDto userLoginDto)
            throws AuthenticationException {
        User user = authenticationService.login(userLoginDto.getLogin(),
                userLoginDto.getPassword());
        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRoles().stream()
                .map(r -> r.getRoleName().name())
                .collect(Collectors.toList()));
        return new ResponseEntity<>(Map.of("token", token), HttpStatus.OK);
    }
}
