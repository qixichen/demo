package com.example.serviceagent.controller;

import com.example.serviceagent.constants.JobConstant;
import com.example.serviceagent.model.Job;
import com.example.serviceagent.model.RestResponse;
import com.example.serviceagent.service.JobExecutionService;
import com.example.serviceagent.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JobControllerTest {

    @Mock
    private JobService jobService;

    @Mock
    private JobExecutionService jobExecutionService;

    @InjectMocks
    private JobController jobController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createJob_ImmediateExecution_Success() {
        Job job = new Job();
        job.setName("Test Job");
        job.setExecutionTime(LocalDateTime.now());
        RestResponse restResponse = new RestResponse("Success", Collections.emptyMap(), 200, true);

        when(jobExecutionService.executeJobNow(any(Job.class))).thenReturn(restResponse);
        doNothing().when(jobService).createJob(any(Job.class));

        RestResponse response = jobController.createJob(job);

        assertNotNull(response);
        assertEquals("Success", response.getBody());
        assertEquals(JobConstant.STATUS_COMPLETED, job.getStatus());
        verify(jobExecutionService, times(1)).executeJobNow(job);
        verify(jobService, times(1)).createJob(job);
    }

    @Test
    void createJob_ScheduledExecution_Success() {
        Job job = new Job();
        job.setName("Test Job");

        doNothing().when(jobService).createJob(any(Job.class));

        RestResponse response = jobController.createJob(job);

        assertNotNull(response);
        assertEquals(JobConstant.STATUS_SCHEDULED, job.getStatus());
        verify(jobExecutionService, never()).executeJobNow(any(Job.class));
        verify(jobService, times(1)).createJob(job);
    }

    @Test
    void getAllJobs_WithFilters_Success() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Job job = new Job();
        job.setCreatedBy("user1");
        Page<Job> jobPage = new PageImpl<>(Collections.singletonList(job));

        when(jobService.findAll(eq(pageRequest), eq(true), eq("user1"))).thenReturn(jobPage);

        Page<Job> result = jobController.getAllJobs(0, 10, true, "user1");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(jobService, times(1)).findAll(pageRequest, true, "user1");
    }

    @Test
    void updateJob_Success() {
        Job job = new Job();
        job.setId(1L);
        job.setCreatedBy("user1");
        job.setFavorite(true);

        when(jobService.updateJob(eq(1L), any(Job.class))).thenReturn(job);

        Job result = jobController.updateJob(1L, job);

        assertNotNull(result);
        assertEquals(true, result.isFavorite());
        verify(jobService, times(1)).updateJob(1L, job);
    }
}