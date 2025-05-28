package org.example.transformerapp.reports.service;

import org.example.transformerapp.execution.dao.TransformationExecutionRepository;
import org.example.transformerapp.execution.model.TransformationExecution;
import org.example.transformerapp.execution.model.TransformationStep;
import org.example.transformerapp.transformer.Transformer;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final TransformationExecutionRepository repository;

    public ReportService(TransformationExecutionRepository repository) {
        this.repository = repository;
    }

    public String generateReport(LocalDateTime from, LocalDateTime to, ReportType format) {
        List<TransformationExecution> executions = repository.findByTimestampBetween(from, to);

        Map<LocalDate, Map<Transformer.Operation, Long>> statistics = executions.stream()
                .collect(Collectors.groupingBy(
                        execution -> execution.getTimestamp().toLocalDate(),
                        Collectors.flatMapping(
                                execution -> execution.getSteps().stream().map(TransformationStep::getOperation),
                                Collectors.groupingBy(operation -> operation, Collectors.counting())
                        )
                ));

        return switch (format) {
            case CSV -> generateCsvReport(statistics);
            case PLAIN -> generatePlainTextReport(statistics);
        };
    }

    public String generateCsvReport(Map<LocalDate, Map<Transformer.Operation, Long>> statistics) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);
        writer.println("Date,Operation,Count");
        statistics.forEach((date, operations) ->
                operations.forEach((operation, count) ->
                        writer.printf("%s,%s,%d%n", date, operation, count)));
        writer.flush();
        return outputStream.toString();
    }

    public String generatePlainTextReport(Map<LocalDate, Map<Transformer.Operation, Long>> statistics) {
        StringBuilder builder = new StringBuilder();
        statistics.forEach((date, operations) -> {
            builder.append("Date: ").append(date).append("\n");
            operations.forEach((operation, count) ->
                    builder.append("  ").append(operation).append(": ").append(count).append("\n"));
        });
        return builder.toString();
    }
}
