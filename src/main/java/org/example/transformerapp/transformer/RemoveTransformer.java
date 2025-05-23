package org.example.transformerapp.transformer;

import java.util.regex.PatternSyntaxException;

import static org.springframework.util.ObjectUtils.isEmpty;

public class RemoveTransformer implements Transformer {

    String regex;

    public RemoveTransformer(String regex) {

        if (isEmpty(regex)) {
            throw new IllegalArgumentException("Regex must be provided for removal");
        }

        this.regex = regex;
    }

    @Override
    public String transform(String input) {

        try {
            return input.replaceAll(regex, "");
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regex pattern: " + regex, e);
        }
    }

    @Override
    public Operation getOperation() {
        return Operation.REMOVE;
    }

    @Override
    public String getParameters() {
        return "{ \"regex\": \"" + regex + "\" }";
    }

}
