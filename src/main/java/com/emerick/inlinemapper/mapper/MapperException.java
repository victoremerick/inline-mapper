package com.emerick.inlinemapper.mapper;

/**
 * Exception thrown when line mapping operations fail.
 */
public class MapperException extends RuntimeException {
    /**
     * Creates an exception with a message.
     *
     * @param message description of the failure
     */
    public MapperException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a message and root cause.
     *
     * @param message description of the failure
     * @param cause   underlying cause
     */
    public MapperException(String message, Throwable cause) {
        super(message + (cause != null ? " | Cause: " + cause.getClass().getSimpleName() + ": " + cause.getMessage() : ""), cause);
    }
}
