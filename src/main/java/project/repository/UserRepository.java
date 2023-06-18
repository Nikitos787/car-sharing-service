package project.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.model.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("FROM User u left join fetch u.roles r where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
}
