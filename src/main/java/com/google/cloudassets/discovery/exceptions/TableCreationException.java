package com.google.cloudassets.discovery.exceptions;

/**
 * This class extends the Exception class and should be thrown when a problem is encountered while
 * trying to create a new table for our spanner db.
 */
public class TableCreationException extends Exception {
    public TableCreationException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
    }
}
