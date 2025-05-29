package org.example.transformerapp.execution.model;

public class TransformationExecutionException extends RuntimeException {

    public TransformationExecutionException(String message) {
        super(message);
    }

    public TransformationExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransformationExecutionException(Throwable cause) {
        super(cause);
    }
}
