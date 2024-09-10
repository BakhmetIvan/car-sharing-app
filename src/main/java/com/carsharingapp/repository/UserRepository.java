package com.carsharingapp.repository;

import com.carsharingapp.model.Role;
import com.carsharingapp.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    List<User> findAllByRolesName(Role.RoleName roleName);
}
