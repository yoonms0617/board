package com.board.user.service;

import com.board.user.domain.User;
import com.board.user.dto.UserSignupRequest;
import com.board.user.exception.DuplicateNicknameException;
import com.board.user.exception.DuplicateUsernameException;
import com.board.user.repository.UserRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입을 한다")
    void signup() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        UserSignupRequest dto = new UserSignupRequest("yoonKun", "yoonms0617", "12345678");
        String encoded = encoder.encode(dto.getPassword());
        User user = new User(dto.getNickname(), dto.getUsername(), encoded);

        given(userRepository.existsByNickname(any(String.class))).willReturn(false);
        given(userRepository.existsByUsername(any(String.class))).willReturn(false);
        given(passwordEncoder.encode(any(String.class))).willReturn(encoded);
        given(userRepository.save(any(User.class))).willReturn(user);

        userService.signup(dto);

        then(userRepository).should(atLeastOnce()).existsByNickname(any(String.class));
        then(userRepository).should(atLeastOnce()).existsByUsername(any(String.class));
        then(passwordEncoder).should(atLeastOnce()).encode(any(String.class));
        then(userRepository).should(atLeastOnce()).save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 닉네임으로 회원가입을 시도할 경우 예외가 발생한다")
    void signup_duplicateNickname() {
        UserSignupRequest dto = new UserSignupRequest("yoonKun", "yoonms0617", "12345678");

        given(userRepository.existsByNickname(any(String.class))).willReturn(true);

        assertThatThrownBy(() -> userService.signup(dto))
                .isInstanceOf(DuplicateNicknameException.class);

        then(userRepository).should(atLeastOnce()).existsByNickname(any(String.class));
        then(userRepository).should(never()).existsByUsername(any(String.class));
        then(passwordEncoder).should(never()).encode(any(String.class));
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 아이디로 회원가입을 시도할 경우 예외가 발생한다")
    void signup_duplicateUsername() {
        UserSignupRequest dto = new UserSignupRequest("yoonKun", "yoonms0617", "12345678");

        given(userRepository.existsByNickname(any(String.class))).willReturn(false);
        given(userRepository.existsByUsername(any(String.class))).willReturn(true);

        assertThatThrownBy(() -> userService.signup(dto))
                .isInstanceOf(DuplicateUsernameException.class);

        then(userRepository).should(atLeastOnce()).existsByNickname(any(String.class));
        then(userRepository).should(atLeastOnce()).existsByUsername(any(String.class));
        then(passwordEncoder).should(never()).encode(any(String.class));
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("닉네임이 존재하는지 확인한다")
    void checkNickname() {
        given(userRepository.existsByNickname(any(String.class))).willReturn(false);

        userService.checkNickname("yoonKun");

        then(userRepository).should(atLeastOnce()).existsByNickname(any(String.class));
    }

    @Test
    @DisplayName("닉네임이 존재하는 경우 예외가 발생한다")
    void checkNickname_duplicate() {
        given(userRepository.existsByNickname(any(String.class))).willReturn(true);

        assertThatThrownBy(() -> userService.checkNickname("yoonKun"))
                .isInstanceOf(DuplicateNicknameException.class);

        then(userRepository).should(atLeastOnce()).existsByNickname(any(String.class));
    }

    @Test
    @DisplayName("아이디가 존재하는지 확인한다")
    void checkUsername() {
        given(userRepository.existsByUsername(any(String.class))).willReturn(false);

        userService.checkUsername("yoonms0617");

        then(userRepository).should(atLeastOnce()).existsByUsername(any(String.class));
    }

    @Test
    @DisplayName("아이디가 존재하는 경우 예외가 발생한다")
    void checkUsername_duplicate() {
        given(userRepository.existsByUsername(any(String.class))).willReturn(true);

        assertThatThrownBy(() -> userService.checkUsername("yoonKun"))
                .isInstanceOf(DuplicateUsernameException.class);

        then(userRepository).should(atLeastOnce()).existsByUsername(any(String.class));
    }

}