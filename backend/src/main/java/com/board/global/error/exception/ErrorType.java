package com.board.global.error.exception;

import lombok.Getter;

import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
public enum ErrorType {

    INVALID_INPUT_VALUE("ERR-COMMON001", HttpStatus.BAD_REQUEST.value()),
    UNSUPPORTED_ERROR_TYPE("ERR-COMMON002", HttpStatus.NOT_FOUND.value()),
    BAD_CREDENTIALS("ERR-AUTH001", HttpStatus.BAD_REQUEST.value()),
    INVALID_TOKEN("ERR-AUTH002", HttpStatus.UNAUTHORIZED.value()),
    EXPIRED_TOKEN("ERR-AUTH003", HttpStatus.UNAUTHORIZED.value()),
    DUPLICATE_NICKNAME("ERR-USER001", HttpStatus.CONFLICT.value()),
    DUPLICATE_USERNAME("ERR-USER002", HttpStatus.CONFLICT.value()),
    USER_NOT_FOUND("ERR-USER003", HttpStatus.NOT_FOUND.value());

    private final String code;
    private final int status;

    ErrorType(String code, int status) {
        this.code = code;
        this.status = status;
    }

    public static ErrorType findByCode(String code) {
        return Arrays.stream(ErrorType.values())
                .filter(errorType -> errorType.getCode().equals(code))
                .findFirst()
                .orElse(UNSUPPORTED_ERROR_TYPE);
    }

}
