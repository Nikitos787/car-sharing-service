package project.controller;

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
    @Operation(summary = "Endpoint for registering a new user",
            description = "Provide data for registration. Make sure that password and "
                    + "repeatPassword are the same")
    public UserResponseDto register(
            @Parameter(schema = @Schema(implementation = UserRegisterRequestDto.class))
            @RequestBody @Valid UserRegisterRequestDto userRegisterDto) {
        User user = authenticationService.register(userRegisterDto.getEmail(),
                userRegisterDto.getPassword(),
                userRegisterDto.getFirstName(),
                userRegisterDto.getLastName());
        notificationService.sendMessageToAdministrators(String
                .format("New user was registered with email: %s", userRegisterDto.getEmail()));
        return responseDtoMapper.mapToDto(user);
    }

    @PostMapping("/login")
    @Operation(summary = "Endpoint for user login",
            description = "Provide login and password for authentication")
    public ResponseEntity<Object> login(
            @Parameter(schema = @Schema(implementation = UserLoginRequestDto.class))
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
