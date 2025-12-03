package com.example.inlinemapper.mapper;

/**
 * Exception thrown when line mapping operations fail.
 */
public class MapperException extends RuntimeException {
    public MapperException(String message) {
        super(message);
    }

    public MapperException(String message, Throwable cause) {
        super(message, cause);
    }
}
