package org.example.transformerapp.transformer;

import org.example.transformerapp.execution.model.TransformationStep;

public interface Transformer {

    String transform(String input);

    Operation getOperation();

    String getParameters();

    default TransformationStep toTransformationStep() {
        return new TransformationStep(getOperation(), getParameters());
    }
}
