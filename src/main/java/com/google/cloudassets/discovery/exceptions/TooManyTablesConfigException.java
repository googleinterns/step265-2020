package com.google.cloudassets.discovery.exceptions;

/**
 * This class extends the Exception class and should be thrown when too many tables are configured
 * for a specific asset kind in our configuration table.
 */
public class TooManyTablesConfigException extends Exception {
    public TooManyTablesConfigException() {
        super();
    }
}
