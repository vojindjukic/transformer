package org.example.transformerapp.execution.controller;

import org.example.transformerapp.execution.dao.TransformationExecutionRepository;
import org.example.transformerapp.execution.model.TransformationExecution;
import org.example.transformerapp.execution.model.TransformationStep;
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
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"transformation.timeout.millis=100"})
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
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("hello everyone"));
    }

    @Test
    void testGetExecutions() {
        var execution = new TransformationExecution(
                null,
                "hell0 world",
                "HELLo WORLD",
                List.of(new TransformationStep(Transformer.Operation.TO_UPPERCASE, "{}"),
                        new TransformationStep(Transformer.Operation.REPLACE, "{\"regex\":\"0\",\"replacement\":\"o\"}")),
                LocalDateTime.now().minusDays(1),
                500L
        );
        repository.save(execution);

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime from = today.minusDays(2);
        String url = String.format("/transformations/executions?from=%s&to=%s", from, today);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("hell0 world"));
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

    @Test
    void testSubmitWithRegexTimeout() {
        // Regex designed to cause catastrophic backtracking
        String vulnerableRegex = "^(a+)+X$";
        String longInput = "a".repeat(10000) + "b";

        String requestBody = String.format("""
                    {
                        "data": "%s",
                        "transformers": [
                            {
                                "operation": "REPLACE",
                                "parameters": {
                                    "regex": "%s",
                                    "replacement": "TOO_LONG"
                                }
                            }
                        ]
                    }
                """, longInput, vulnerableRegex);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/transformations/submit", request, String.class);

        System.out.println("Response: " + response.getBody());

        assertNotNull(response);
        assertEquals(HttpStatus.REQUEST_TIMEOUT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Transformation exceeded the maximum allowed time"));
    }

    @Test
    void testSubmitWithInputExceedingMaxLength() {
        String longInput = "a".repeat(10001);

        String requestBody = String.format("""
                    {
                        "data": "%s",
                        "transformers": [
                            {
                                "operation": "TO_UPPERCASE",
                                "parameters": {}
                            }
                        ]
                    }
                """, longInput);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/transformations/submit", request, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Input data exceeds the maximum allowed length"));
    }
}
