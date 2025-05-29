package org.example.transformerapp.transformer;

public class RegexOperationInterruptedException extends RuntimeException {

    public RegexOperationInterruptedException(String message) {
        super(message);
    }

    public RegexOperationInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegexOperationInterruptedException(Throwable cause) {
        super(cause);
    }
}
