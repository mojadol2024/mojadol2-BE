package com.gnu.pbl2.exception.handler;

import com.gnu.pbl2.exception.GeneralException;
import com.gnu.pbl2.response.code.BaseErrorCode;

public class CoverLetterHandler extends GeneralException {

    public CoverLetterHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
