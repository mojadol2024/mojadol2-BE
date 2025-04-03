package com.gnu.pbl2.exception.handler;

import com.gnu.pbl2.exception.GeneralException;
import com.gnu.pbl2.response.code.BaseErrorCode;

public class InterviewHandler extends GeneralException {

    public InterviewHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
