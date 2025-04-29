package com.gnu.pbl2.exception.handler;

import com.gnu.pbl2.exception.GeneralException;
import com.gnu.pbl2.response.code.BaseErrorCode;

public class PaymentHandler extends GeneralException {

    public PaymentHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
