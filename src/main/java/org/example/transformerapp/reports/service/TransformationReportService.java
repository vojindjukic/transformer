package org.example.transformerapp.reports.service;

import org.example.transformerapp.execution.dao.TransformationExecutionRepository;
import org.example.transformerapp.execution.model.TransformationExecution;
import org.example.transformerapp.execution.model.TransformationStep;
import org.example.transformerapp.transformer.Operation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransformationReportService {

    private final TransformationExecutionRepository repository;

    private final Map<ReportType, ReportFormatter> formatters;

    public TransformationReportService(TransformationExecutionRepository repository, Set<ReportFormatter> formatters) {
        this.repository = repository;
        this.formatters = formatters.stream()
                .collect(Collectors.toMap(ReportFormatter::getType, formatter -> formatter));
    }

    public String generateReport(LocalDateTime from, LocalDateTime to, ReportType format) {

        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        List<TransformationExecution> executions = repository.findByTimestampBetween(from, to);

        Map<LocalDate, Map<Operation, Long>> statistics = executions.stream()
                .collect(Collectors.groupingBy(
                        execution -> execution.getTimestamp().toLocalDate(),
                        Collectors.flatMapping(
                                execution -> execution.getSteps().stream().map(TransformationStep::getOperation),
                                Collectors.groupingBy(operation -> operation, Collectors.counting())
                        )
                ));

        ReportFormatter formatter = formatters.get(format);
        if (formatter == null) {
            throw new IllegalArgumentException("Unsupported report format: " + format);
        }
        return formatter.format(statistics);
    }
}
