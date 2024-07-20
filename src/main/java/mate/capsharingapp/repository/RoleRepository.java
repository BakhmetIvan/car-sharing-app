package mate.capsharingapp.repository;

import java.util.Set;
import mate.capsharingapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findByName(Role.RoleName name);
}
