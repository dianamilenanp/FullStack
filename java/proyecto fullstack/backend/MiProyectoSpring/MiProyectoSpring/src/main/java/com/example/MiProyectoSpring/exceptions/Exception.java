package com.ptesa.bmc.coreservice.exception;

/**
 * Interface for grouping custom exception for BMC Poject.
 */
public abstract class Exception extends RuntimeException {

    /**
     * Constructor.
     * @param message Exception message.
     */
    protected Exception(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param message Exception message.
     * @param cause Exception cause.
     */
    protected Exception(String message, Throwable cause) {
        super(message, cause);
    }

}
