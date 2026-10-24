package com.g3.sppts.util;

/** Unchecked wrapper for SQL errors so servlets do not have to catch SQLException everywhere. */
public class DataAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DataAccessException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
