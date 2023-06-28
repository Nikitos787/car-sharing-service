package project.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import project.model.user.Role;
import project.model.user.RoleName;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ContainersConfig.class)
class RoleRepositoryTest {
    private static final Long ID = 1L;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldReturnUserWithExistingRole_ok() {
        Role actual = roleRepository.findByRoleName(RoleName.CUSTOMER).orElseThrow();
        assertEquals(RoleName.CUSTOMER.name(), actual.getRoleName().name());
        assertEquals(ID, actual.getId());
    }
}
