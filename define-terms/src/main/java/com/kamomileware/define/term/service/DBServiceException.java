package com.kamomileware.define.term.service;

import java.io.IOException;

/**
 * Created by pepe on 20/08/14.
 */
public class DBServiceException extends RuntimeException {

    public DBServiceException() {
    }

    public DBServiceException(String message) {
        super(message);
    }

    public DBServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBServiceException(Throwable cause) {
        super(cause);
    }

    public DBServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
