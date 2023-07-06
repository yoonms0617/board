package com.board.user.exception;

import com.board.global.error.exception.BaseException;
import com.board.global.error.exception.ErrorType;

public class DuplicateUsernameException extends BaseException {

    public DuplicateUsernameException() {
        super(ErrorType.DUPLICATE_USERNAME);
    }

}
