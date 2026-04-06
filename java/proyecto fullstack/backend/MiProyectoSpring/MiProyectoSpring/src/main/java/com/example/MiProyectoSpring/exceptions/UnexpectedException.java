package com.ptesa.bmc.coreservice.exception;

import lombok.Getter;

/**
 * Exception for unexpected errors.

 */

@Getter
public class UnexpectedException extends Exception {

    // Fields

    private final ErrorCode errorCode;
    private final Throwable cause;


    // Logic

    public UnexpectedException(ErrorCode errorCode, Throwable cause, String errorDetail) {
        super(errorDetail, cause);
        this.errorCode = errorCode;
        this.cause = cause;
    }

    public UnexpectedException(ErrorCode errorCode, String errorDetail) {
        super(errorDetail);
        this.errorCode = errorCode;
        this.cause = null;
    }
}
