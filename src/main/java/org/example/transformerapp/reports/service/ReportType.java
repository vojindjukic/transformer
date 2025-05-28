package org.example.transformerapp.reports.service;

public enum ReportType {
    CSV("csv"),
    PLAIN("txt");

    private final String extension;

    ReportType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
