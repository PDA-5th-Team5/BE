package com.pda.utilservice.response.exception.handler;

import com.pda.utilservice.response.code.BaseErrorCode;
import com.pda.utilservice.response.exception.GeneralException;

public class GeneralHandler extends GeneralException {
    public GeneralHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
