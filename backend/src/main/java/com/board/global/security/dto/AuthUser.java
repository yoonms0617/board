package com.board.global.security.dto;

import lombok.Getter;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

@Getter
public class AuthUser extends User {

    private final String nickname;
    private final String role;

    public AuthUser(String nickname, String username, String password, String role) {
        super(username, password, AuthorityUtils.createAuthorityList(role));
        this.nickname = nickname;
        this.role = role;
    }

}
