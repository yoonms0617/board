package com.board.global.security.exception;

import com.board.global.error.exception.ErrorType;
import org.springframework.security.core.AuthenticationException;

public class ExpiredTokenException extends AuthenticationException {

    public ExpiredTokenException() {
        super(ErrorType.EXPIRED_TOKEN.getCode());
    }

}
