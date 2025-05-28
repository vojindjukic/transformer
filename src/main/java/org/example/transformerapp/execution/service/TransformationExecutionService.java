package org.example.transformerapp.execution.service;

import org.example.transformerapp.execution.dao.TransformationExecutionRepository;
import org.example.transformerapp.execution.model.TransformationExecution;
import org.example.transformerapp.transformer.Transformer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class TransformationExecutionService {

    public record TransformationExecutionRequest(String data, List<Transformer> transformers) {}

    private final TransformationExecutionRepository transformationRepository;

    @Value("${maxNumOfTransformersPerExecution:10}")
    private int maxNumOfTransformers;

    public TransformationExecutionService(TransformationExecutionRepository transformationRepository) {
        this.transformationRepository = transformationRepository;
    }

    public List<TransformationExecution> getTransformations(LocalDateTime from, LocalDateTime to) {

        validateDateRange(from, to);

        if (from == null) {
            from = LocalDateTime.now().minusDays(7);
        }
        if (to == null) {
            to = LocalDateTime.now();
        }

        return transformationRepository.findByTimestampBetween(from, to);
    }

    private void validateDateRange(LocalDateTime from, LocalDateTime to) {

        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }

    public TransformationExecution submit(TransformationExecutionRequest request) {

        validateTransformationRequest(request);

        TransformationExecution transformationExecution = process(request);

        return transformationRepository.save(transformationExecution);
    }

    private TransformationExecution process(TransformationExecutionRequest request) {

        long startTime = System.nanoTime();

        String transformedData = request.data();
        for (Transformer transformer : request.transformers()) {
            transformedData = transformer.transform(transformedData);
        }

        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        return new TransformationExecution(
                null,
                request.data(),
                transformedData,
                request.transformers().stream().map(Transformer::toTransformationStep).toList(),
                LocalDateTime.now(),
                executionTime
        );
    }

    private void validateTransformationRequest(TransformationExecutionRequest request) {
        if (isEmpty(request.data())) {
            throw new IllegalArgumentException("Input data cannot be empty");
        }

        if (isEmpty(request.transformers())) {
            throw new IllegalArgumentException("Transformers cannot be empty");
        }

        if (request.transformers().size() > maxNumOfTransformers) {
            throw new IllegalArgumentException("Number of transformers exceeds the maximum allowed: " + maxNumOfTransformers);
        }
    }
}
