package com.example.serviceagent.controller;

import com.example.serviceagent.constants.JobConstant;
import com.example.serviceagent.model.ApiResponse;
import com.example.serviceagent.model.Job;
import com.example.serviceagent.service.JobExecutionService;
import com.example.serviceagent.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;
    private final JobExecutionService jobExecutionService;

    public JobController(JobService jobService, JobExecutionService jobExecutionService) {
        this.jobService = jobService;
        this.jobExecutionService = jobExecutionService;
    }

    @PostMapping
    public ApiResponse createJob(@RequestBody Job job) {
        ApiResponse apiResponse = new ApiResponse();
        if (job.getExecutionTime() != null) {
            try {
                apiResponse = jobExecutionService.executeJobNow(job);
                job.setStatus(JobConstant.STATUS_COMPLETED);
            } catch (Exception e) {
                job.setStatus(JobConstant.STATUS_FAILED);
            }
        } else {
            job.setStatus(JobConstant.STATUS_SCHEDULED);
        }
        jobService.createJob(job);
        return apiResponse;
    }

    @GetMapping
    public Page<Job> getAllJobs(@RequestParam int page, @RequestParam int size, Boolean favorite) {
        return jobService.findAll(PageRequest.of(page, size), favorite);
    }

    @GetMapping("/{id}")
    public Job getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }

    @PutMapping("/{id}")
    public Job updateJob(@PathVariable Long id, @RequestBody Job job) {
        return jobService.updateJob(id, job);
    }
}