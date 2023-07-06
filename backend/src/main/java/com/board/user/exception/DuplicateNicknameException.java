package com.board.user.exception;

import com.board.global.error.exception.BaseException;
import com.board.global.error.exception.ErrorType;

public class DuplicateNicknameException extends BaseException {

    public DuplicateNicknameException() {
        super(ErrorType.DUPLICATE_NICKNAME);
    }

}
