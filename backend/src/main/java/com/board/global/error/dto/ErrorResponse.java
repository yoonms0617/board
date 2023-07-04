package com.board.global.error.dto;

import com.board.global.error.exception.ErrorType;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String code;
    private final int status;

    private ErrorResponse(ErrorType errorType) {
        this.code = errorType.getCode();
        this.status = errorType.getStatus();
    }

    public static ErrorResponse of(ErrorType errorType) {
        return new ErrorResponse(errorType);
    }

}
