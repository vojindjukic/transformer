package org.example.transformerapp.reports.controller;

import org.example.transformerapp.reports.service.TransformationReportService;
import org.example.transformerapp.reports.service.ReportType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/transformations")
public class TransformationReportController {

    private final TransformationReportService transformationReportService;

    public TransformationReportController(TransformationReportService transformationReportService) {
        this.transformationReportService = transformationReportService;
    }

    @GetMapping("/reports")
    public ResponseEntity<byte[]> getTransformationReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam ReportType format) {

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(23, 59, 59);

        String report = transformationReportService.generateReport(fromDateTime, toDateTime, format);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transformation_report." + format.getExtension());

        return ResponseEntity.ok().headers(headers).body(report.getBytes());
    }
}
