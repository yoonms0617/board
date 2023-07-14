package com.board.global.security.exception;

import com.board.global.error.exception.ErrorType;
import org.springframework.security.core.AuthenticationException;

public class InvalidTokenException extends AuthenticationException {

    public InvalidTokenException() {
        super(ErrorType.INVALID_TOKEN.getCode());
    }

}
