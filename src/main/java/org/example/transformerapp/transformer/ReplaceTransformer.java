package org.example.transformerapp.transformer;

import java.util.regex.PatternSyntaxException;

import static org.springframework.util.ObjectUtils.isEmpty;

public class ReplaceTransformer implements Transformer {

    String regex;
    String replacement;

    public ReplaceTransformer(String regex, String replacement) {

        if (isEmpty(regex)) {
            throw new IllegalArgumentException("Regex must be provided for replacement");
        }

        if (isEmpty(replacement)) {
            throw new IllegalArgumentException("Replacement string must be provided");
        }

        this.regex = regex;
        this.replacement = replacement;
    }

    @Override
    public String transform(String input) {
        try {
            return input.replaceAll(regex, replacement);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regex pattern: " + regex, e);
        }
    }

    @Override
    public Operation getOperation() {
        return Operation.REPLACE;
    }

    @Override
    public String getParameters() {
        return "{ \"regex\": \"" + regex + "\", \"replacement\": \"" + replacement + "\" }";
    }
}
