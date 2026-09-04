package com.VlixAli.paleo.repository;

import com.VlixAli.paleo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=none")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByKeycloakUserIdReturnsExistingUser() {
        userRepository.save(User.builder()
                .keycloakUserId("kc-alice")
                .username("alice")
                .displayName("Alice")
                .build());

        assertThat(userRepository.findByKeycloakUserId("kc-alice"))
                .isPresent()
                .get()
                .extracting(User::getUsername)
                .isEqualTo("alice");
    }

    @Test
    void findByKeycloakUserIdUnknownReturnsEmpty() {
        assertThat(userRepository.findByKeycloakUserId("kc-unknown")).isEmpty();
    }
}
