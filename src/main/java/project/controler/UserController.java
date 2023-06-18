package project.controler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.dto.request.UserRequestDto;
import project.dto.response.UserResponseDto;
import project.model.user.Role;
import project.model.user.RoleName;
import project.model.user.User;
import project.service.NotificationService;
import project.service.RoleService;
import project.service.UserService;
import project.service.mapper.RequestDtoMapper;
import project.service.mapper.ResponseDtoMapper;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ResponseDtoMapper<UserResponseDto, User> responseDtoMapper;
    private final RequestDtoMapper<UserRequestDto, User> requestDtoMapper;
    private final RoleService roleService;
    private final NotificationService notificationService;

    @PutMapping("/{id}/role")
    @Operation(summary = "endpoint for manager and updating role at user")
    public UserResponseDto updateRole(
            @Parameter(description = "User id", schema = @Schema(type = "Integer",
                    defaultValue = "1"))
            @PathVariable Long id,
            @Parameter(description = "role Name", schema = @Schema(type = "String",
                    defaultValue = "MANAGER"))
            @RequestParam String role) {
        User userById = userService.findById(id);
        Role byRoleName = roleService.findByRoleName(RoleName.valueOf(role));
        Set<Role> roles = new HashSet<>();
        roles.add(byRoleName);
        userById.setRoles(roles);
        notificationService
                .sendMessageToAdministrators(String
                        .format("User with id: %s was updated with role: %s", id, role));
        return responseDtoMapper.mapToDto(userService.update(userById));
    }

    @GetMapping("/me")
    @Operation(description = "endpoint for getting your own information")
    public UserResponseDto getMe(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return responseDtoMapper.mapToDto(userService.findByEmail(userDetails
                .getUsername()).orElseThrow(()
                        -> new NoSuchElementException("Can't find user by email")));
    }

    @PutMapping("/me")
    @Operation(summary = "endpoint for update info",
            description = "endpoint for update your own information (except your role)")
    public UserResponseDto updateInfo(Authentication authentication,
                                      @Parameter(schema = @Schema(type = "String",
                                              defaultValue = "{\n"
                                                      + "\"email\":\"your new email\", \n"
                                                      + "\"password\":\"your new password\", \n"
                                                      + "\"firstName\":\"your new First name\", \n"
                                                      + "\"lastName\":\"your new Last name\" }"))
                                      @RequestBody UserRequestDto userRequestDto) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User userToUpdate = userService.findByEmail(userDetails.getUsername()).orElseThrow(()
                -> new NoSuchElementException("Can't find user by email"));
        User user = requestDtoMapper.mapToModel(userRequestDto);
        user.setId(userToUpdate.getId());
        user.setRoles(userToUpdate.getRoles());
        notificationService
                .sendMessageToAdministrators(String
                        .format("User with id: %s info was updated by: %s",
                                userToUpdate.getId(), authentication.getName()));
        return responseDtoMapper.mapToDto(userService.update(user));
    }
}
