package org.example.transformerapp.execution.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class TransformationExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private String input;
    @Lob
    private String output;

    @ElementCollection
    private List<TransformationStep> steps;

    private LocalDateTime timestamp;
    private Long executionTimeNano;

    public TransformationExecution(Long id, String input, String output, List<TransformationStep> steps, LocalDateTime timestamp, long executionTimeNano) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.steps = steps;
        this.timestamp = timestamp;
        this.executionTimeNano = executionTimeNano;
    }

    public TransformationExecution() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getExecutionTimeNano() {
        return executionTimeNano;
    }

    public void setExecutionTimeNano(Long executionTime) {
        this.executionTimeNano = executionTime;
    }

    public List<TransformationStep> getSteps() {
        return steps;
    }

    public void setSteps(List<TransformationStep> steps) {
        this.steps = steps;
    }
}

