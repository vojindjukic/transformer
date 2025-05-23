package org.example.transformerapp.transformer;

public class LowercaseTransformer implements Transformer {

    @Override
    public String transform(String input) {
        return input.toLowerCase();
    }

    @Override
    public Operation getOperation() {
        return Operation.TO_LOWERCASE;
    }

    @Override
    public String getParameters() {
        return "{}";
    }
}
