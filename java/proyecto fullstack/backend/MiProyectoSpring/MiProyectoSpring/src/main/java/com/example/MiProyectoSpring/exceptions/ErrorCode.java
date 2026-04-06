package com.ptesa.bmc.coreservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Defines error codes.

 */
@Getter
public enum ErrorCode {

    // General error codes

    UNEXPECTED_ERROR("00000", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("00001", HttpStatus.BAD_REQUEST),
    WRITING_TO_DISK("00002", HttpStatus.INTERNAL_SERVER_ERROR),
    READ_FROM_DISK("00003", HttpStatus.INTERNAL_SERVER_ERROR),
    ENTITY_NOT_FOUND("00004", HttpStatus.BAD_REQUEST),
    USER_DOES_NOT_HAVE_ACCESS_TO_RESOURCE("00005", HttpStatus.FORBIDDEN),
    PROPERTY_NOT_FOUND("00006", HttpStatus.BAD_REQUEST),
    CANNOT_READ_FILE_FROM_DISK("00007", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_DOES_NOT_HAVE_ACCESS("0008", HttpStatus.FORBIDDEN),


    // Fields

    private final String code;
    private final HttpStatus httpStatus;


    // Logic

    ErrorCode(final String code, final HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
