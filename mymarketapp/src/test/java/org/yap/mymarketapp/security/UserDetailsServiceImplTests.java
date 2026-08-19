package org.yap.mymarketapp.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserDetailsServiceImplTests {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void findByUsername_ShouldLoadUserFromDatabase() {
        userDetailsService.findByUsername("user")
                .as(StepVerifier::create)
                .assertNext(userDetails -> {
                    assertThat(userDetails.getUsername()).isEqualTo("user");
                    assertThat(userDetails.getPassword()).isNotBlank();
                    assertThat(passwordEncoder.matches("password", userDetails.getPassword())).isTrue();
                })
                .verifyComplete();
    }

    @Test
    void findByUsername_ShouldCompleteWithError_WhenUserDoesNotExist() {
        userDetailsService.findByUsername("no_such_user")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }
}