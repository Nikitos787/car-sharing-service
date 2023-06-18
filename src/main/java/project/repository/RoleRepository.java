package project.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.model.user.Role;
import project.model.user.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("FROM Role r WHERE r.roleName = :roleName")
    Optional<Role> findByRoleName(@Param("roleName") RoleName roleName);

}
