package org.example.transformerapp.transformer;

public class UppercaseTransformer implements Transformer {

    @Override
    public String transform(String input) {
        return input.toUpperCase();
    }

    @Override
    public Operation getOperation() {
        return Operation.TO_UPPERCASE;
    }

    @Override
    public String getParameters() {
        return "{}";
    }
}
