package com.VlixAli.paleo.repository;

import com.VlixAli.paleo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByKeycloakId(String keycloakId);

    boolean existsByUsername(String username);
}
