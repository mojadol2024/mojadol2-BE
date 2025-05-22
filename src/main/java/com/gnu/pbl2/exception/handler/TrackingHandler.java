package com.gnu.pbl2.exception.handler;

import com.gnu.pbl2.exception.GeneralException;
import com.gnu.pbl2.response.code.BaseErrorCode;

public class TrackingHandler extends GeneralException {

    public TrackingHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
