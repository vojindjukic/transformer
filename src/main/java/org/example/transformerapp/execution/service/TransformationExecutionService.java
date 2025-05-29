package org.example.transformerapp.execution.service;

import org.example.transformerapp.execution.dao.TransformationExecutionRepository;
import org.example.transformerapp.execution.model.TransformationExecution;
import org.example.transformerapp.transformer.Transformer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class TransformationExecutionService {

    public record TransformationExecutionRequest(String data, List<Transformer> transformers) {}

    private final TransformationExecutionRepository transformationRepository;

    private final ExecutorService regexThreadPool;

    @Value("${transformation.maxNumOfTransformers:10}")
    private int maxNumOfTransformers;

    @Value("${transformation.maxInputLengthChars:10000}")
    private int maxInputLengthChars;

    @Value("${transformation.timeout.millis:3000}")
    private long regexTimeoutMillis;

    public TransformationExecutionService(TransformationExecutionRepository transformationRepository, ExecutorService regexThreadPool) {
        this.transformationRepository = transformationRepository;
        this.regexThreadPool = regexThreadPool;
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

        var transformationExecutionFuture = regexThreadPool.submit(() -> process(request));

        TransformationExecution transformationExecution;
        try {
            transformationExecution = transformationExecutionFuture.get(regexTimeoutMillis, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            transformationExecutionFuture.cancel(true);
            throw new TransformationExecutionException("Transformation execution exceeded time limit: " + e.getMessage(), e);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new TransformationExecutionException("Transformation execution failed: " + e.getMessage(), e);
        }

        return transformationRepository.save(transformationExecution);
    }

    private TransformationExecution process(TransformationExecutionRequest request) {

        long startTime = System.nanoTime();

        String currentTransformedData = request.data();

        for (Transformer transformer : request.transformers()) {
            currentTransformedData = transformer.transform(currentTransformedData);
        }

        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        return new TransformationExecution(
                null,
                request.data(),
                currentTransformedData,
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

        if (request.data().length() > maxInputLengthChars) {
            throw new IllegalArgumentException("Input data exceeds the maximum allowed length: " + maxInputLengthChars);
        }

        if (request.transformers().size() > maxNumOfTransformers) {
            throw new IllegalArgumentException("Number of transformers exceeds the maximum allowed: " + maxNumOfTransformers);
        }
    }
}
