package project.security;

import static org.springframework.security.core.userdetails.User.withUsername;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import project.model.user.User;
import project.service.UserService;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByEmail(username).orElseThrow(()
                -> new UsernameNotFoundException(String
                .format("User with username: %s doesn't found in DB", username)));
        UserBuilder userBuilder = withUsername(username);
        userBuilder.password(user.getPassword());
        userBuilder.roles(user.getRoles().stream()
                .map(r -> r.getRoleName().name())
                .toArray(String[]::new));
        return userBuilder.build();
    }
}
