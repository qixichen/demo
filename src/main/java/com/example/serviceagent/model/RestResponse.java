package com.example.serviceagent.model;

import java.util.Map;

public class RestResponse {
    private String body;
    private Map<String, String> headers;
    private int statusCode;
    private boolean success;

    public RestResponse(String body, Map<String, String> headers, int statusCode, boolean success) {
        this.body = body;
        this.headers = headers;
        this.statusCode = statusCode;
        this.success = success;
    }

    public RestResponse() {

    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
