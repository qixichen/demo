package com.example.serviceagent.service;

import com.example.serviceagent.constants.JobConstant;
import com.example.serviceagent.model.HeaderInfo;
import com.example.serviceagent.model.Job;
import com.example.serviceagent.model.RestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class JobExecutionServiceTest {

    @Mock
    private JobService jobService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private JobExecutionService jobExecutionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void executeJobNow_Success() {
        Job job = new Job();
        job.setApiUrl("http://test.com");
        job.setMethod("GET");
        job.setHeaderInfos(Collections.singletonList(new HeaderInfo("Content-Type", "application/json")));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        ResponseEntity<String> responseEntity = ResponseEntity.ok()
                .header("Response-Header", "value")
                .body("Success");

        when(restTemplate.exchange(eq("http://test.com"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        RestResponse response = jobExecutionService.executeJobNow(job);

        assertNotNull(response);
        assertEquals("Success", response.getBody());
        assertEquals(200, response.getStatusCode());
        assertTrue(response.isSuccess());
        assertEquals("value", response.getHeaders().get("Response-Header"));
        verify(restTemplate, times(1)).exchange(eq("http://test.com"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void executeScheduledJobs_Success() {
        Job job = new Job();
        job.setId(1L);
        job.setName("Test Job");
        job.setStatus(JobConstant.STATUS_SCHEDULED);
        job.setApiUrl("http://test.com");
        job.setMethod("GET");
        List<Job> jobs = Collections.singletonList(job);
        ResponseEntity<String> responseEntity = ResponseEntity.ok("Success");

        when(jobService.findByStatusAndScheduledTimeBefore(any(), any())).thenReturn(jobs);
        when(restTemplate.exchange(eq("http://test.com"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);
        when(jobService.updateJob(eq(1L), any(Job.class))).thenReturn(job);

        jobExecutionService.executeScheduledJobs();

        assertEquals(JobConstant.STATUS_COMPLETED, job.getStatus());
        assertNotNull(job.getExecutionTime());
        verify(jobService, times(1)).findByStatusAndScheduledTimeBefore(any(), any());
        verify(restTemplate, times(1)).exchange(eq("http://test.com"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
        verify(jobService, times(1)).updateJob(eq(1L), any(Job.class));
    }
}