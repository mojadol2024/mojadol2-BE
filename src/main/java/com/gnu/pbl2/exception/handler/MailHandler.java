package com.gnu.pbl2.exception.handler;

import com.gnu.pbl2.exception.GeneralException;
import com.gnu.pbl2.response.code.BaseErrorCode;

public class MailHandler extends GeneralException {

    public MailHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
