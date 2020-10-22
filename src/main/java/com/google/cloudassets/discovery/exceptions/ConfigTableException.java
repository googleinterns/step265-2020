package com.google.cloudassets.discovery.exceptions;

/**
 * This class extends the Exception class and should be thrown when something in our configuration
 * table is not configured properly.
 */
public class ConfigTableException extends Exception {
    public ConfigTableException(String errorMsg) {
        super(errorMsg);
    }
}
