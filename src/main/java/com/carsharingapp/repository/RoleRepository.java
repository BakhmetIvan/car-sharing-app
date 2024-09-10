package com.carsharingapp.repository;

import com.carsharingapp.model.Role;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findByName(Role.RoleName name);
}
