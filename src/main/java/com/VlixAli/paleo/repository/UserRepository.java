package com.VlixAli.paleo.repository;

import com.VlixAli.paleo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByKeycloakUserId(String keycloakUserId);

    boolean existsByUsername(String username);
}
