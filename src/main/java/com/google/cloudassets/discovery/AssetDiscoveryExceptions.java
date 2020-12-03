package com.google.cloudassets.discovery;

/**
 * This class contains all of the exception classes that are specific for the asset discovery project.
 */
public class AssetDiscoveryExceptions {
    /**
     * This class extends the Exception class and should be thrown when something in our configuration
     * table is not configured properly.
     */
    public static class ConfigTableException extends Exception {
        public ConfigTableException(String errorMsg, Throwable cause) {
            super(errorMsg, cause);
        }
    }

    /**
     * This class extends the Exception class and should be thrown when a problem is encountered while
     * trying to create a new table for our spanner db.
     */
    public static class TableCreationException extends Exception {
        public TableCreationException(String errorMsg, Throwable cause) {
            super(errorMsg, cause);
        }
    }

    /**
     * This class extends the Exception class and should be thrown when a problem is encountered while
     * trying to inset data into our spanner db tables.
     */
    public static class TableInsertionException extends Exception {
        public TableInsertionException(String errorMsg, Throwable cause) {
            super(errorMsg, cause);
        }
    }

    /**
     * This class extends the Exception class and should be thrown when no table is configured for a
     * specific asset kind in our configuration table.
     */
    public static class NoTableConfigException extends Exception {
        public NoTableConfigException() {
            super();
        }
    }

    /**
     * This class extends the Exception class and should be thrown when too many tables are configured
     * for a specific asset kind in our configuration table.
     */
    public static class TooManyTablesConfigException extends Exception {
        public TooManyTablesConfigException() {
            super();
        }
    }
}
