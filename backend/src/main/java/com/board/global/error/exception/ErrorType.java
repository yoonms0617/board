package com.board.global.error.exception;

import lombok.Getter;

import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {

    INVALID_INPUT_VALUE("ERR-COMMON001", HttpStatus.BAD_REQUEST.value()),
    BAD_CREDENTIALS("ERR-AUTH001", HttpStatus.BAD_REQUEST.value()),
    DUPLICATE_NICKNAME("ERR-USER001", HttpStatus.CONFLICT.value()),
    DUPLICATE_USERNAME("ERR-USER002", HttpStatus.CONFLICT.value());

    private final String code;
    private final int status;

    ErrorType(String code, int status) {
        this.code = code;
        this.status = status;
    }

}
