package com.google.cloudassets.discovery.exceptions;

/**
 * This class extends the Exception class and should be thrown when no table is configured for a
 * specific asset kind in our configuration table.
 */
public class NoTableConfigException extends Exception {
    public NoTableConfigException() {
        super();
    }
}
