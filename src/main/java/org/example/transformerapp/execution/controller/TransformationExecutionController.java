package org.example.transformerapp.execution.controller;

import jakarta.validation.Valid;
import org.example.transformerapp.execution.model.TransformationExecution;
import org.example.transformerapp.execution.service.TransformationExecutionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/transformations")
public class TransformationExecutionController {

    private final TransformationExecutionConverter converter;
    private final TransformationExecutionService transformationExecutionService;

    public TransformationExecutionController(TransformationExecutionConverter converter, TransformationExecutionService transformationExecutionService) {
        this.converter = converter;
        this.transformationExecutionService = transformationExecutionService;
    }


    @PostMapping("/submit")
    public TransformationExecution submitForTransformation(@RequestBody @Valid TransformationExecutionConverter.TransformationExecutionRequestDTO request) {
        var internalRequest = converter.convertToInternalRequest(request);
        return transformationExecutionService.submit(internalRequest);
    }

    @GetMapping("/executions")
    public List<TransformationExecution> getExecutions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return transformationExecutionService.getTransformations(from, to);
    }
}
