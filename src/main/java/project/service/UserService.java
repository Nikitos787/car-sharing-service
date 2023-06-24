package project.service;

import java.util.List;
import project.model.user.RoleName;
import project.model.user.User;

public interface UserService {
    User save(User user);

    User update(User user);

    User findById(Long id);

    User findByEmail(String email);

    List<User> findByRoles(RoleName roleName);

}
