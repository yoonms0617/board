package com.board.user.repository;

import com.board.global.config.JpaAuditConfig;
import com.board.user.domain.User;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles(profiles = "test")
@Import(JpaAuditConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원을 저장한다")
    void user_save() {
        User user = new User("yoonKun", "yoonms0617", "12345678");

        User actual = userRepository.save(user);

        assertThat(actual.getId()).isNotNull();
    }

    @Test
    @DisplayName("nickname의 존재 여부를 확인한다")
    void user_existsByNickname() {
        User user = new User("yoonKun", "yoonms0617", "12345678");
        userRepository.save(user);

        boolean actual = userRepository.existsByNickname("yoonKun");

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("username의 존재 여부를 확인한다")
    void user_existsByUsername() {
        User user = new User("yoonKun", "yoonms0617", "12345678");
        userRepository.save(user);

        boolean actual = userRepository.existsByUsername("yoonms0617");

        assertThat(actual).isTrue();
    }

}