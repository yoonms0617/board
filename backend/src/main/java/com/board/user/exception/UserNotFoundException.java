package com.board.user.exception;

import com.board.global.error.exception.BaseException;
import com.board.global.error.exception.ErrorType;

public class UserNotFoundException extends BaseException {

    public UserNotFoundException() {
        super(ErrorType.USER_NOT_FOUND);
    }

}
