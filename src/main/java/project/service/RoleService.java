package project.service;

import project.model.user.Role;
import project.model.user.RoleName;

public interface RoleService {
    Role save(Role role);

    void delete(Long id);

    Role findByRoleName(RoleName roleName);
}
