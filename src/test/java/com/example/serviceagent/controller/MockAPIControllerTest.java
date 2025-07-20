package com.example.serviceagent.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class MockAPIControllerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private MockAPIController mockAPIController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void echoPost_Success() throws IOException {
        when(request.getMethod()).thenReturn("POST");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("test body")));

        ResponseEntity<Map<String, Object>> response = mockAPIController.echoPost(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("POST", response.getBody().get("method"));
        assertEquals("test body", response.getBody().get("body"));
    }

    @Test
    void echoGet_Success() throws IOException {
        when(request.getMethod()).thenReturn("GET");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));

        ResponseEntity<Map<String, Object>> response = mockAPIController.echoGet(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("GET", response.getBody().get("method"));
        assertEquals("", response.getBody().get("body"));
    }

    @Test
    void echoPut_Success() throws IOException {
        when(request.getMethod()).thenReturn("PUT");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("test body")));

        ResponseEntity<Map<String, Object>> response = mockAPIController.echoPut(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("PUT", response.getBody().get("method"));
        assertEquals("test body", response.getBody().get("body"));
    }

    @Test
    void echoDelete_Success() throws IOException {
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("test body")));

        ResponseEntity<Map<String, Object>> response = mockAPIController.echoDelete(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("DELETE", response.getBody().get("method"));
        assertEquals("test body", response.getBody().get("body"));
    }
}