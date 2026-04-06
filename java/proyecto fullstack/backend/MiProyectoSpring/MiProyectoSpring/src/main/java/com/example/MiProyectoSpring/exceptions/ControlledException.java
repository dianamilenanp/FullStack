package com.ptesa.bmc.coreservice.exception;

import lombok.Getter;

import java.util.Map;

/**
 * Exception for controlled errors that could occur.
 */

@Getter
public class ControlledException extends Exception {

    // Fields

    private final ErrorCode errorCode;
    private final Throwable cause;

    /**
     * Custom data information for exception
     */
    private final Map<String, String> data;

    // Logic

    public ControlledException(ErrorCode errorCode, Throwable cause, String errorDetail) {
        super(errorDetail, cause);
        this.errorCode = errorCode;
        this.cause = cause;
        this.data = null;
    }

    public ControlledException(ErrorCode errorCode, String errorDetail) {
        super(errorDetail);
        this.errorCode = errorCode;
        this.cause = null;
        this.data = null;
    }

    public ControlledException(ErrorCode errorCode, String errorDetail, Map<String, String> data) {
        super(errorDetail);
        this.errorCode = errorCode;
        this.data = data;
        this.cause = null;
    }
}
