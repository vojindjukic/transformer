package org.example.transformerapp.execution.controller;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.transformerapp.execution.service.TransformationExecutionService.TransformationExecutionRequest;
import org.example.transformerapp.transformer.LowercaseTransformer;
import org.example.transformerapp.transformer.Operation;
import org.example.transformerapp.transformer.RemoveTransformer;
import org.example.transformerapp.transformer.ReplaceTransformer;
import org.example.transformerapp.transformer.Transformer;
import org.example.transformerapp.transformer.UppercaseTransformer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TransformationExecutionConverter {

    public record TransformationExecutionRequestDTO(
            @NotNull @JsonProperty("data") String data,
            @NotEmpty @JsonProperty("transformers") List<TransformerDTO> transformers) {}

    public record TransformerDTO(Operation operation, Map<String, String> parameters) {}

    public TransformationExecutionRequest convertToInternalRequest(TransformationExecutionRequestDTO request) {

        List<Transformer> transformers = request.transformers().stream()
                .map(transformerRequest -> {
                    Operation operation = transformerRequest.operation();
                    Map<String, String> parameters = transformerRequest.parameters();
                    return createTransformer(operation, parameters);
                })
                .toList();

        return new TransformationExecutionRequest(request.data(), transformers);

    }

    private Transformer createTransformer(Operation operation, Map<String, String> parameters) {
        return switch (operation) {
            case REMOVE:
                String regex = parameters.get("regex");
                yield new RemoveTransformer(regex);
            case REPLACE:
                String target = parameters.get("regex");
                String replacement = parameters.get("replacement");
                yield new ReplaceTransformer(target, replacement);
            case TO_UPPERCASE:
                yield new UppercaseTransformer();
            case TO_LOWERCASE:
                yield new LowercaseTransformer();
        };
    }
}
