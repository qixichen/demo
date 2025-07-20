package com.example.serviceagent.service;

import com.example.serviceagent.model.Job;
import com.example.serviceagent.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createJob_Success() {
        Job job = new Job();
        job.setName("Test Job");

        when(jobRepository.save(any(Job.class))).thenReturn(job);

        jobService.createJob(job);

        verify(jobRepository, times(1)).save(job);
    }

    @Test
    void getJobById_Success() {
        Job job = new Job();
        job.setId(1L);
        job.setCreatedBy("user1");

        when(jobRepository.findByIdAndCreatedBy(1L, "user1")).thenReturn(Optional.of(job));

        Job result = jobService.getJobById(1L, "user1");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(jobRepository, times(1)).findByIdAndCreatedBy(1L, "user1");
    }

    @Test
    void getJobById_NotFound() {
        when(jobRepository.findByIdAndCreatedBy(1L, "user1")).thenReturn(Optional.empty());

        Job result = jobService.getJobById(1L, "user1");

        assertNull(result);
        verify(jobRepository, times(1)).findByIdAndCreatedBy(1L, "user1");
    }

    @Test
    void findAll_WithFilters_Success() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Job job = new Job();
        job.setCreatedBy("user1");
        job.setFavorite(true);
        Page<Job> jobPage = new PageImpl<>(Collections.singletonList(job));

        when(jobRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(jobPage);

        Page<Job> result = jobService.findAll(pageRequest, true, "user1");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(jobRepository, times(1)).findAll(any(Specification.class), eq(pageRequest));
    }

    @Test
    void findByStatusAndScheduledTimeBefore_Success() {
        Job job = new Job();
        job.setStatus("SCHEDULED");
        LocalDateTime now = LocalDateTime.now();
        List<Job> jobs = Collections.singletonList(job);

        when(jobRepository.findByStatusAndScheduledTimeBefore("SCHEDULED", now)).thenReturn(jobs);

        List<Job> result = jobService.findByStatusAndScheduledTimeBefore("SCHEDULED", now);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(jobRepository, times(1)).findByStatusAndScheduledTimeBefore("SCHEDULED", now);
    }

    @Test
    void updateJob_Success() {
        Job existingJob = new Job();
        existingJob.setId(1L);
        existingJob.setCreatedBy("user1");
        Job updatedJob = new Job();
        updatedJob.setId(1L);
        updatedJob.setCreatedBy("user1");
        updatedJob.setFavorite(true);
        updatedJob.setStatus("COMPLETED");

        when(jobRepository.findByIdAndCreatedBy(1L, "user1")).thenReturn(Optional.of(existingJob));
        when(jobRepository.save(any(Job.class))).thenReturn(existingJob);

        Job result = jobService.updateJob(1L, updatedJob);

        assertNotNull(result);
        assertTrue(result.isFavorite());
        assertEquals("COMPLETED", result.getStatus());
        verify(jobRepository, times(1)).findByIdAndCreatedBy(1L, "user1");
        verify(jobRepository, times(1)).save(existingJob);
    }

    @Test
    void updateJob_NotFound() {
        Job updatedJob = new Job();
        updatedJob.setId(1L);
        updatedJob.setCreatedBy("user1");

        when(jobRepository.findByIdAndCreatedBy(1L, "user1")).thenReturn(Optional.empty());

        Job result = jobService.updateJob(1L, updatedJob);

        assertNull(result);
        verify(jobRepository, times(1)).findByIdAndCreatedBy(1L, "user1");
        verify(jobRepository, never()).save(any(Job.class));
    }
}