package org.example.transformerapp.reports.service;

import org.example.transformerapp.transformer.Operation;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
public class CsvReportFormatter implements ReportFormatter {

    @Override
    public String format(Map<LocalDate, Map<Operation, Long>> statistics) {
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Date,Operation,Count\n");

        for (Map.Entry<LocalDate, Map<Operation, Long>> dateEntry : statistics.entrySet()) {
            LocalDate date = dateEntry.getKey();
            for (Map.Entry<Operation, Long> operationEntry : dateEntry.getValue().entrySet()) {
                Operation operation = operationEntry.getKey();
                Long count = operationEntry.getValue();
                csvBuilder.append(date).append(",").append(operation).append(",").append(count).append("\n");
            }
        }

        return csvBuilder.toString();
    }

    @Override
    public ReportType getType() {
        return ReportType.CSV;
    }
}
