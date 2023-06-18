package project.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.model.user.RoleName;
import project.model.user.User;
import project.repository.UserRepository;
import project.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        User userFromDb = userRepository.findById(user.getId()).orElseThrow(()
                -> new NoSuchElementException("User doesn't exist in DB for updating"));
        userFromDb.setEmail(user.getEmail());
        userFromDb.setPassword(passwordEncoder.encode(user.getPassword()));
        userFromDb.setFirstName(user.getFirstName());
        userFromDb.setLastName(user.getLastName());
        userFromDb.setRoles(user.getRoles());
        userFromDb.setChatId(user.getChatId());
        return userRepository.save(userFromDb);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(()
                -> new NoSuchElementException(String
                .format("User with id: %s doesn't exist in DB", id)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findByRoles(RoleName roleName) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles()
                        .stream()
                        .anyMatch(role -> role.getRoleName() == roleName))
                .collect(Collectors.toList());
    }
}
