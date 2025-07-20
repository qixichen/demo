package com.example.serviceagent.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mock/api/echo")
@Tag(name = "Mock API", description = "Endpoints to mock API responses and return request details")
public class MockAPIController {

    @PostMapping
    @Operation(summary = "Echo POST request", description = "Returns the method, headers and body of the POST request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully echoed the request", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
            })
    })
    public ResponseEntity<Map<String, Object>> echoPost(
            @Parameter(hidden = true) HttpServletRequest request) throws IOException {
        return echoRequest(request);
    }

    @GetMapping
    @Operation(summary = "Echo GET request", description = "Returns the method and headers of the GET request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully echoed the request", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
            })
    })
    public ResponseEntity<Map<String, Object>> echoGet(
            @Parameter(hidden = true) HttpServletRequest request) throws IOException {
        return echoRequest(request);
    }

    @PutMapping
    @Operation(summary = "Echo PUT request", description = "Returns the method, headers and body of the PUT request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully echoed the request", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
            })
    })
    public ResponseEntity<Map<String, Object>> echoPut(
            @Parameter(hidden = true) HttpServletRequest request) throws IOException {
        return echoRequest(request);
    }

    @DeleteMapping
    @Operation(summary = "Echo DELETE request", description = "Returns the method, headers and body of the DELETE request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully echoed the request", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
            })
    })
    public ResponseEntity<Map<String, Object>> echoDelete(
            @Parameter(hidden = true) HttpServletRequest request) throws IOException {
        return echoRequest(request);
    }

    private ResponseEntity<Map<String, Object>> echoRequest(HttpServletRequest request) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headerMap.put(headerName, request.getHeader(headerName));
        }

        StringBuilder bodyBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                bodyBuilder.append(line);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("method", request.getMethod());
        response.put("headers", headerMap);
        response.put("body", bodyBuilder.toString());

        return ResponseEntity.ok(response);
    }
}
