package com.example.serviceagent.service;

import com.example.serviceagent.constants.JobConstant;
import com.example.serviceagent.model.ApiResponse;
import com.example.serviceagent.model.HeaderInfo;
import com.example.serviceagent.model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobExecutionService {
    private static final Logger logger = LoggerFactory.getLogger(JobExecutionService.class);


    private final JobService jobService;
    private final RestTemplate restTemplate;

    public JobExecutionService(JobService jobService, RestTemplate restTemplate) {
        this.jobService = jobService;
        this.restTemplate = restTemplate;
    }

    public ApiResponse executeJobNow(Job job) {
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                job.getApiUrl(),
                HttpMethod.valueOf(job.getMethod()),
                new HttpEntity<>(job.getBody(), convertHeaders(job.getHeaderInfos())),
                String.class);

        Map<String, String> headerMap = new HashMap<>();
        responseEntity.getHeaders().forEach((key, value) -> {
            if (!value.isEmpty()) headerMap.put(key, value.get(0));
        });
        return new ApiResponse(
                responseEntity.getBody(),
                headerMap,
                responseEntity.getStatusCodeValue(),
                responseEntity.getStatusCode().is2xxSuccessful()
        );

    }

    @Scheduled(fixedRate = 60000)
    public void executeScheduledJobs() {
        LocalDateTime now = LocalDateTime.now();
        List<Job> scheduledJobs = jobService.findByStatusAndScheduledTimeBefore(JobConstant.STATUS_SCHEDULED, now);

        for (Job job : scheduledJobs) {
            try {
                executeJobNow(job);
                job.setStatus(JobConstant.STATUS_COMPLETED);
            } catch (Exception e) {
                job.setStatus(JobConstant.STATUS_FAILED);
            }
            job.setExecutionTime(now);
            jobService.updateJob(job.getId(), job);
        }
    }

    private HttpHeaders convertHeaders(List<HeaderInfo> headerInfos) {
        HttpHeaders headers = new HttpHeaders();
        if (headerInfos != null) {
            for (HeaderInfo header : headerInfos) {
                if (header.getKey() != null && header.getValue() != null) {
                    headers.add(header.getKey(), header.getValue());
                }
            }
        }
        return headers;
    }
}