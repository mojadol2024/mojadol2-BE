package com.gnu.pbl2.exception.handler;

import com.gnu.pbl2.exception.GeneralException;
import com.gnu.pbl2.response.code.BaseErrorCode;

public class QuestionHandler extends GeneralException {

    public QuestionHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
