package com.gnu.pbl2.exception.handler;

import com.gnu.pbl2.exception.GeneralException;
import com.gnu.pbl2.response.code.BaseErrorCode;

public class UserHandler extends GeneralException {

    public UserHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
