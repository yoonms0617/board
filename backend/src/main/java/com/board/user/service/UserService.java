package com.board.user.service;

import com.board.user.domain.User;
import com.board.user.dto.UserSignupRequest;
import com.board.user.exception.DuplicateNicknameException;
import com.board.user.exception.DuplicateUsernameException;
import com.board.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(UserSignupRequest dto) {
        checkNickname(dto.getNickname());
        checkUsername(dto.getUsername());
        String encoded = passwordEncoder.encode(dto.getPassword());
        User user = User.builder()
                .nickname(dto.getNickname())
                .username(dto.getUsername())
                .password(encoded)
                .build();
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void checkNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new DuplicateNicknameException();
        }
    }

    @Transactional(readOnly = true)
    public void checkUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException();
        }
    }

}
