package org.example.transformerapp.reports;

import org.example.transformerapp.execution.dao.TransformationExecutionRepository;
import org.example.transformerapp.execution.model.TransformationExecution;
import org.example.transformerapp.execution.model.TransformationStep;
import org.example.transformerapp.transformer.Transformer;
import org.example.transformerapp.transformer.Transformer.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TransformationExecutionRepository repository;


    @BeforeEach
    void setup() {
        repository.deleteAll();

        var execution1 = new TransformationExecution(
                null,
                "hell0 world",
                "HELLo WORLD",
                List.of(new TransformationStep(Transformer.Operation.TO_UPPERCASE, "{}"),
                        new TransformationStep(Transformer.Operation.REPLACE, "{\"regex\":\"0\",\"replacement\":\"o\"}")),
                LocalDateTime.now().minusDays(1),
                500L
        );

        var execution2 = new TransformationExecution(
                null,
                "HELLO WORLD",
                "hi world",
                List.of(new TransformationStep(Operation.TO_LOWERCASE, "{}"),
                        new TransformationStep(Operation.REPLACE, "{\"regex\":\"hello\",\"replacement\":\"hi\"}")),
                LocalDateTime.now().minusDays(1),
                300L
        );

        repository.saveAll(List.of(execution1, execution2));

    }

    @Test
    void testGetReportInCsvFormat() {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(27);
        String url = String.format("/transformations/reports?from=%s&to=%s&format=CSV", from, today);

        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        String reportContent = new String(response.getBody());
        assertTrue(reportContent.contains("TO_UPPERCASE,1"));
        assertTrue(reportContent.contains("REPLACE,2"));
        assertTrue(reportContent.contains("TO_LOWERCASE,1"));
    }

    @Test
    void testGetReportInPlainTextFormat() {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(27);
        String url = String.format("/transformations/reports?from=%s&to=%s&format=PLAIN", from, today);

        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        String reportContent = new String(response.getBody());
        assertTrue(reportContent.contains("TO_UPPERCASE: 1"));
        assertTrue(reportContent.contains("REPLACE: 2"));
        assertTrue(reportContent.contains("TO_LOWERCASE: 1"));
    }

    @Test
    void testGetReportWithInvalidFormat() {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(27);
        String url = String.format("/transformations/reports?from=%s&to=%s&format=INVALID", from, today);

        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
