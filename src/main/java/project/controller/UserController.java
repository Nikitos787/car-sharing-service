package project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashSet;
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
    @Operation(summary = "Endpoint for updating the role of a user by a manager")
    public UserResponseDto updateRole(
            @Parameter(description = "User ID")
            @PathVariable Long id,
            @Parameter(description = "Role Name")
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
    @Operation(description = "Endpoint to get your own information")
    public UserResponseDto getMe(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return responseDtoMapper.mapToDto(userService.findByEmail(userDetails.getUsername()));
    }

    @PutMapping("/me")
    @Operation(summary = "Endpoint to update information",
            description = "Endpoint to update your own information (except your role)")
    public UserResponseDto updateInfo(Authentication authentication,
                                      @Parameter(schema = @Schema(implementation =
                                              UserRequestDto.class))
                                      @RequestBody UserRequestDto userRequestDto) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User userToUpdate = userService.findByEmail(userDetails.getUsername());
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
