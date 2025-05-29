package org.example.transformerapp.reports.service;

import org.example.transformerapp.transformer.Operation;

import java.time.LocalDate;
import java.util.Map;

public interface ReportFormatter {

    String format(Map<LocalDate, Map<Operation, Long>> statistics);

    ReportType getType();
}
