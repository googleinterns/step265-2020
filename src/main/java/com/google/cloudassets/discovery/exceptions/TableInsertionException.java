package com.google.cloudassets.discovery.exceptions;

/**
 * This class extends the Exception class and should be thrown when a problem is encountered while
 * trying to inset data into our spanner db tables.
 */
public class TableInsertionException extends Exception {
    public TableInsertionException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
    }
}
