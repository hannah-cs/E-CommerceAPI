package com.startsteps.Final.Project.ECommerce.security.login.repository;

import java.util.Optional;

import com.startsteps.Final.Project.ECommerce.security.login.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<User> findById(Integer id);
}
