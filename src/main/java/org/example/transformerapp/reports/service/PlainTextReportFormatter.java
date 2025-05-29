package org.example.transformerapp.reports.service;

import org.example.transformerapp.transformer.Operation;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
public class PlainTextReportFormatter implements ReportFormatter {

    @Override
    public String format(Map<LocalDate, Map<Operation, Long>> statistics) {
        StringBuilder report = new StringBuilder();
        for (Map.Entry<LocalDate, Map<Operation, Long>> entry : statistics.entrySet()) {
            LocalDate date = entry.getKey();
            Map<Operation, Long> operations = entry.getValue();
            report.append("Date: ").append(date).append("\n");
            for (Map.Entry<Operation, Long> operationEntry : operations.entrySet()) {
                report.append("  Operation: ").append(operationEntry.getKey())
                        .append(", Count: ").append(operationEntry.getValue()).append("\n");
            }
        }
        return report.toString();
    }

    @Override
    public ReportType getType() {
        return ReportType.PLAIN;
    }
}
