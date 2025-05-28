package org.example.transformerapp.execution.controller;

import org.example.transformerapp.execution.dao.TransformationExecutionRepository;
import org.example.transformerapp.execution.model.TransformationExecution;
import org.example.transformerapp.transformer.Transformer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransformationExecutionControllerTest {

    private static final HttpHeaders headers = new HttpHeaders();

    @BeforeAll
    static void setup() {
        headers.set("Content-Type", "application/json");
    }

    @AfterEach
    void cleanup() {
        repository.deleteAll();
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TransformationExecutionController controller;

    @Autowired
    private TransformationExecutionRepository repository;

    @Test
    void testSubmitForTransformation() {
        String requestBody = """
                    {
                        "data": "hell0 world",
                        "transformers": [
                            {
                                "operation": "TO_UPPERCASE",
                                "parameters": {}
                            }
                        ]
                    }
                """;
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/transformations/submit", request, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("HELL0 WORLD"));
    }

    @Test
    void testSubmitWithMultipleTransformers() {
        String requestBody = """
                    {
                        "data": "hello world",
                        "transformers": [
                            {
                                "operation": "TO_UPPERCASE",
                                "parameters": {}
                            },
                            {
                                "operation": "REPLACE",
                                "parameters": {
                                    "regex": "WORLD",
                                    "replacement": "EVERYONE"
                                }
                            },
                            {
                                "operation": "TO_LOWERCASE",
                                "parameters": {}
                            }
                        ]
                    }
                """;

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("/transformations/submit", request, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("hello everyone"));
    }

    @Test
    void testGetExecutions() {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(1);
        LocalDate to = today;
        String url = String.format("/transformations/executions?from=%s&to=%s", from, to);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSubmitForTransformationWithInvalidInput() {
        String requestBody = """
                    {
                        "data": "",
                        "transformers": []
                    }
                """;

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/transformations/submit", request, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    void testGetExecutionsWithInvalidDateRange() {
        String url = "/transformations/executions?from=2025-12-31T23:59:59&to=2024-12-31T00:00:00";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetExecutionsWithDefaultDateRange() {
        String url = "/transformations/executions";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void testSubmitMultipleTransformationsAndFetch() {
        var request1 =
                new TransformationExecutionConverter.TransformationExecutionRequestDTO(
                        "hello world",
                        List.of(new TransformationExecutionConverter.TransformerDTO(
                                Transformer.Operation.TO_UPPERCASE, null)));
        var request2 =
                new TransformationExecutionConverter.TransformationExecutionRequestDTO(
                        "HELLO WORLD",
                        List.of(new TransformationExecutionConverter.TransformerDTO(
                                Transformer.Operation.TO_LOWERCASE, null)));

        controller.submitForTransformation(request1);
        controller.submitForTransformation(request2);

        List<TransformationExecution> executions = controller.getExecutions(null, null);
        assertNotNull(executions);
        assertEquals(2, executions.size());
        assertTrue(executions.stream().anyMatch(execution -> execution.getInput().equals("hello world") && execution.getOutput().equals("HELLO WORLD")));
        assertTrue(executions.stream().anyMatch(execution -> execution.getInput().equals("HELLO WORLD") && execution.getOutput().equals("hello world")));
    }

    @Test
    void testExceedingMaxTransformers() {
        StringBuilder transformers = new StringBuilder("[");
        transformers.append(("{" +
                "\"operation\": \"TO_UPPERCASE\", \"parameters\": {}" +
                "},").repeat(11));
        transformers.deleteCharAt(transformers.length() - 1).append("]");

        String requestBody = """
            {
                "data": "hello world",
                "transformers":\s""" + transformers + """
            }
        """;

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/transformations/submit", request, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Number of transformers exceeds the maximum allowed"));
    }
}
