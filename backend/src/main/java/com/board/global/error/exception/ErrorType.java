package com.board.global.error.exception;

import lombok.Getter;

@Getter
public enum ErrorType {

    ;

    private final String code;
    private final int status;

    ErrorType(String code, int status) {
        this.code = code;
        this.status = status;
    }

}
