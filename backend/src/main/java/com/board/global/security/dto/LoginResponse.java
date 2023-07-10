package com.board.global.security.dto;

import lombok.Getter;

@Getter
public class LoginResponse {

    private final String nickname;
    private final Token token;

    public LoginResponse(String nickname, String accessToken) {
        this.nickname = nickname;
        this.token = new Token(accessToken);
    }

    @Getter
    private static class Token {

        private final String accessToken;

        public Token(String accessToken) {
            this.accessToken = accessToken;
        }

    }

}
