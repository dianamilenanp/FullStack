package com.ptesa.bmc.coreservice.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.ptesa.bmc.coreservice.dtos.common.GenericResponse;
import com.ptesa.bmc.coreservice.modules.registerevent.exceptions.RegisterEventDianRejectionException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.*;
import java.util.concurrent.RejectedExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Error handler for all applications that implement the error-handling-service-client,
 * automatically for all REST Services that implement the client.
 */
@RestControllerAdvice()
@Slf4j
public class GeneralExceptionHandler {

    // Constants

    private static final String POSTGRES_CUSTOM_FORBIDDEN_CODE = "PFORB";
    public static final String UNEXPECTED_ERROR_DETAIL = "Unexpected error occurred. Please check that the request is correct or contact PTESA support for further information.";
    private static final String STANDARD_MESSAGE_FORMAT = "%s: %s";
    private static final String TOO_MANY_REQUESTS = "Too many requests received. This request has been aborted.";
    private static final String OPTIMISTIC_LOCK_EXCEPTION_DETAIL = "An optimistic lock exception occurred.";


    /**
     * Handle controlled exception by logging the message
     * exception (not the stack trace) and returning and according
     * response.
     * @param exception Exception to be handled.
     * @return Response including the error code, error detail and http status.
     */
    @ExceptionHandler(value = {ControlledException.class})
    public ResponseEntity<GenericResponse> handleErrorException(ControlledException exception) {
        log.error(exception.getMessage());
        GenericResponse.Error error = GenericResponse.Error.of(exception.getErrorCode().getCode(), exception.getMessage(), exception.getData());
        GenericResponse response = GenericResponse.builder()
                .status(GenericResponse.ERROR)
                .errors(List.of(error))
                .build();
        return new ResponseEntity<>(response, exception.getErrorCode().getHttpStatus());
    }

    /**
     * Handle unexpected exception by logging the message
     * exception (not the stack trace) and returning and according
     * response.
     * @param exception Exception to be handled.
     * @return Response including the error code, error detail and http status.
     */
    @ExceptionHandler(value = {UnexpectedException.class})
    public ResponseEntity<GenericResponse> handleErrorException(UnexpectedException exception) {
        log.error(exception.getMessage(), exception);
        GenericResponse.Error error = GenericResponse.Error.of(exception.getErrorCode().getCode(), UNEXPECTED_ERROR_DETAIL);
        GenericResponse response = GenericResponse.builder()
                .status(GenericResponse.ERROR)
                .errors(List.of(error))
                .build();
        return new ResponseEntity<>(response, exception.getErrorCode().getHttpStatus());
    }

    /**
     * Handle exception related with register event rejection.
     * @param exception Exception to be handled.
     * @return Response with the error code, error detail and http status.
     */
    @ExceptionHandler(value = {RegisterEventDianRejectionException.class})
    public ResponseEntity<GenericResponse> handleRegisterEventDianRejectionException(RegisterEventDianRejectionException exception) {
        log.error(exception.getMessage(), exception);
        final Pattern pattern = Pattern.compile("Regla:\\s*(.*?),\\s*Rechazo:\\s*(.*)");
        Map<String, String> messages = exception.getErrorMessages().stream()
                .map(errorMessage -> {
                    Matcher matcher = pattern.matcher(errorMessage);
                    if (matcher.find()) {
                        return Map.entry(matcher.group(1), matcher.group(2));
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        GenericResponse.Error error = GenericResponse.Error.of(exception.getErrorCode().getCode(), exception.getMessage(), messages);
        GenericResponse response = GenericResponse.builder()
                .status(GenericResponse.ERROR)
                .errors(List.of(error))
                .build();
        return new ResponseEntity<>(response, exception.getErrorCode().getHttpStatus());
    }

    /**
     * Handle exception related with data binding validations.
     * @param exception Exception to be handled.
     * @return Response with the fields that failed validations
     */
    @ExceptionHandler(value = {BindException.class})
    public ResponseEntity<GenericResponse> handleBindException(BindException exception) {
        log.error(exception.getMessage());
        List<GenericResponse.Error> errors = new ArrayList<>();

        // Adds the list of errors produced by data type validations.
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            GenericResponse.Error error = GenericResponse.Error.of(ErrorCode.BAD_REQUEST.getCode(), String.format(STANDARD_MESSAGE_FORMAT, fieldError.getField(), fieldError.getDefaultMessage()));
            errors.add(error);
        }

        // Add errors that are not related to fields
        for (ObjectError objectError : exception.getBindingResult().getGlobalErrors()) {
            GenericResponse.Error error = GenericResponse.Error.of(ErrorCode.BAD_REQUEST.getCode(), objectError.getDefaultMessage());
            errors.add(error);
        }

        GenericResponse response = GenericResponse.builder()
                .status(GenericResponse.ERROR)
                .errors(errors)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    /**
     * Handle exception related with data constraints validations.
     * @param exception Exception to be handled.
     * @return Response with the fields that failed validations
     */
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<GenericResponse> handleException(ConstraintViolationException exception) {
        List<GenericResponse.Error> errors = new ArrayList<>();

        // Adds the list of errors produced by data type validations.
        exception.getConstraintViolations().forEach(constraintViolation -> {
            GenericResponse.Error error = GenericResponse.Error.of(ErrorCode.BAD_REQUEST.getCode(), String.format(STANDARD_MESSAGE_FORMAT, constraintViolation.getPropertyPath(), constraintViolation.getMessage()));
            errors.add(error);
        });

        GenericResponse response = GenericResponse.builder()
                .status(GenericResponse.ERROR)
                .errors(errors)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    /**
     * Handle exception related with data constraints validations.
     * @param exception Exception to be handled.
     * @return Response with the fields that failed validations
     */
    @ExceptionHandler(value = {CustomConstraintViolationException.class})
    public ResponseEntity<GenericResponse> handleException(CustomConstraintViolationException exception) {
        Set<CustomConstraintViolation> violations = exception.getConstraintViolations();
        List<GenericResponse.Error> errors = new ArrayList<>();

        // Adds the list of errors produced by data type validations.
        violations.forEach(customConstraintViolation -> {
            String message = null;
            if (customConstraintViolation.getPropertyPathPrefix() != null && !customConstraintViolation.getPropertyPathPrefix().isBlank()) {
                message = String.format("%s.%s: %s", customConstraintViolation.getPropertyPathPrefix(), customConstraintViolation.getConstraintViolation().getPropertyPath(), customConstraintViolation.getConstraintViolation().getMessage());
            }
            else {
                message = String.format(STANDARD_MESSAGE_FORMAT, customConstraintViolation.getConstraintViolation().getPropertyPath(), customConstraintViolation.getConstraintViolation().getMessage());
            }

            GenericResponse.Error error = GenericResponse.Error.of(ErrorCode.BAD_REQUEST.getCode(), message);
            errors.add(error);
        });

        GenericResponse response = GenericResponse.builder()
                .status(GenericResponse.ERROR)
                .errors(errors)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    /**
     * Method to detect errors by validating the required fields in the request
     * @param exception Handle exception
     * @return Returns the detail of the message and the http error
     */
    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<GenericResponse> handleBindException(HttpMessageNotReadableException exception) {
        log.error(exception.getMessage());

        String message = exception.getCause() instanceof JsonMappingException jsonMappingException && jsonMappingException.getCause() instanceof ControlledException controlledException
                ? controlledException.getMessage()
                : "The request is not valid. Please check it out and change it accordingly.";

        GenericResponse.Error error = GenericResponse.Error.of(ErrorCode.BAD_REQUEST.getCode(), message);
        GenericResponse response = GenericResponse.builder()
                .status(GenericResponse.ERROR)
                .errors(List.of(error))
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Capture exceptions with error code PFORB and return FORBIDDEN HTTP code. If the error
     * code is not PFORB, an internal server error is returned.
     * @param exception Exception to be handled.
     * @return Response including the error code, error detail and http status.
     */
    @ExceptionHandler(value = {JpaSystemException.class})
    public ResponseEntity<GenericResponse> handleErrorException(JpaSystemException exception) {
        Throwable rootCause = exception.getRootCause();
        if (rootCause instanceof PSQLException psqlException && psqlException.getServerErrorMessage().getSQLState().equals(POSTGRES_CUSTOM_FORBIDDEN_CODE)) {
            log.error(psqlException.getServerErrorMessage().getMessage());
            GenericResponse.Error error = GenericResponse.Error.of(ErrorCode.USER_DOES_NOT_HAVE_ACCESS_TO_RESOURCE.getCode(), psqlException.getServerErrorMessage().getMessage());
            GenericResponse response = GenericResponse.builder()
                    .status(GenericResponse.ERROR)
                    .errors(List.of(error))
                    .build();
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        log.error(exception.getMessage(), exception);
        GenericResponse.Error error = GenericResponse.Error.of(ErrorCode.UNEXPECTED_ERROR.getCode(), UNEXPECTED_ERROR_DETAIL);
        GenericResponse response = GenericResponse.builder()
                .status(GenericResponse.ERROR)
                .errors(List.of(error))
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Method to detect validation errors in the request and build a response accordingly.
     * @param exception Handle exception
     * @return Returns the detail of the message and the http error
     */
    @ExceptionHandler(value = {HandlerMethodValidationException.class})
    public ResponseEntity<GenericResponse> handleErrorException(HandlerMethodValidationException exception) {
        List<GenericResponse.Error> errors = new ArrayList<>();

        exception.getAllValidationResults().forEach(validationResult ->
            validationResult.getResolvableErrors().forEach(messageSourceResolvable -> {
                GenericResponse.Error error = this.getError(messageSourceResolvable);
                errors.add(error);
            })
        );

        GenericResponse response = GenericResponse.builder()
                .status(GenericResponse.ERROR)
                .errors(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle exception related with optimistic locking failure.
     * @param exception Exception to be handled.
     * @return Response with the error code, error detail and http status.
     */
    @ExceptionHandler(value = {ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<GenericResponse> handleErrorException(ObjectOptimisticLockingFailureException exception) {
        log.warn(exception.getMessage());
        GenericResponse.Error error = GenericResponse.Error.of(ErrorCode.OPTIMISTIC_LOCK_EXCEPTION.getCode(), OPTIMISTIC_LOCK_EXCEPTION_DETAIL);
        GenericResponse response = GenericResponse.builder()
                .status(GenericResponse.ERROR)
                .errors(List.of(error))
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = {RejectedExecutionException.class})
    public ResponseEntity<GenericResponse> handleErrorException(RejectedExecutionException exception) {
        log.warn(exception.getMessage());
        GenericResponse.Error error = GenericResponse.Error.of(ErrorCode.TOO_MANY_REQUESTS.getCode(), TOO_MANY_REQUESTS);
        GenericResponse response = GenericResponse.builder()
                .status(GenericResponse.ERROR)
                .errors(List.of(error))
                .build();
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handle unexpected exception by logging the message
     * exception (not the stack trace) and returning and according
     * response.
     * @param exception Exception to be handled.
     * @return Response including the error code, error detail and http status.
     */
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<GenericResponse> handleErrorException(Exception exception) {
        log.error(exception.getMessage(), exception);
        GenericResponse.Error error = GenericResponse.Error.of(ErrorCode.UNEXPECTED_ERROR.getCode(), UNEXPECTED_ERROR_DETAIL);
        GenericResponse response = GenericResponse.builder()
                .status(GenericResponse.ERROR)
                .errors(List.of(error))
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    /**
     * Get the error from the message source resolvable
     * @param messageSourceResolvable
     * @return Error for the message source resolvable.
     */
    private GenericResponse.Error getError(MessageSourceResolvable messageSourceResolvable) {
        Object[] arguments = messageSourceResolvable.getArguments();
        GenericResponse.Error error;
        if (arguments != null && arguments.length > 0) {
            DefaultMessageSourceResolvable defaultMessageSourceResolvable =  (DefaultMessageSourceResolvable)arguments[0];
            String field = defaultMessageSourceResolvable.getDefaultMessage();
            error = GenericResponse.Error.of(ErrorCode.BAD_REQUEST.getCode(), String.format(STANDARD_MESSAGE_FORMAT, field, messageSourceResolvable.getDefaultMessage()));
        }
        else {
            error = GenericResponse.Error.of(ErrorCode.BAD_REQUEST.getCode(), String.format(STANDARD_MESSAGE_FORMAT, "Unknown field", "Unknown error"));
        }
        return error;
    }
}

