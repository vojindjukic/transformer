package org.example.transformerapp.execution.model;

import jakarta.persistence.Embeddable;
import org.example.transformerapp.transformer.Transformer.Operation;

@Embeddable
public class TransformationStep {

    private Operation operation;
    private String parameters;

    public TransformationStep(Operation operation, String parameters) {
        this.operation = operation;
        this.parameters = parameters;
    }

    public TransformationStep() {
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
}
