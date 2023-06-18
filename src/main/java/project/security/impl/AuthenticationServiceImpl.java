package project.security.impl;

import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.exception.AuthenticationException;
import project.model.user.RoleName;
import project.model.user.User;
import project.security.AuthenticationService;
import project.service.RoleService;
import project.service.UserService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Override
    public User register(String email, String password, String firstName, String lastName) {
        User userToSave = new User();
        userToSave.setEmail(email);
        userToSave.setPassword(password);
        userToSave.setFirstName(firstName);
        userToSave.setLastName(lastName);
        userToSave.setRoles(Set.of(roleService.findByRoleName(RoleName.CUSTOMER)));
        return userService.save(userToSave);
    }

    @Override
    public User login(String email, String password) throws AuthenticationException {
        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            throw new AuthenticationException("Incorrect email or password");
        }
        return user.get();
    }
}
