package com.board.global.security.handler;

import com.board.global.security.dto.AuthUser;
import com.board.global.security.dto.LoginResponse;
import com.board.global.security.util.JwtUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        String accessToken = jwtUtil.createAccessToken(authUser.getUsername(), authUser.getRole());
        LoginResponse loginResponse = new LoginResponse(authUser.getNickname(), accessToken);
        objectMapper.writeValue(response.getWriter(), loginResponse);
    }

}
