package com.board.global.security.handler;

import com.board.global.error.dto.ErrorResponse;
import com.board.global.error.exception.ErrorType;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        ErrorResponse errorResponse = null;
        if (exception instanceof BadCredentialsException) {
            errorResponse = ErrorResponse.of(ErrorType.BAD_CREDENTIALS);
        }
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

}
