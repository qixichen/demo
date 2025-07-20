package com.example.serviceagent.controller;

import com.example.serviceagent.constants.JobConstant;
import com.example.serviceagent.model.RestResponse;
import com.example.serviceagent.model.Job;
import com.example.serviceagent.service.JobExecutionService;
import com.example.serviceagent.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
@Tag(name = "Job Management", description = "APIs for managing scheduled and executed jobs")
public class JobController {
    private static final Logger logger = LoggerFactory.getLogger(JobController.class);
    private final JobService jobService;
    private final JobExecutionService jobExecutionService;

    public JobController(JobService jobService, JobExecutionService jobExecutionService) {
        this.jobService = jobService;
        this.jobExecutionService = jobExecutionService;
    }

    @PostMapping
    @Operation(summary = "Create a new job", description = "Creates a job. If executionTime is set, it will run immediately.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Job created successfully", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
        })
    })
    public RestResponse createJob(@RequestBody @Parameter(description = "Job object to be created") Job job) {
        RestResponse restResponse = new RestResponse();
        if (job.getExecutionTime() != null) {
            try {
                restResponse = jobExecutionService.executeJobNow(job);
            } catch (Exception e) {
                logger.error("Error executing job: {}", job.getName(), e);
            }
            job.setStatus(JobConstant.STATUS_COMPLETED);
        } else {
            job.setStatus(JobConstant.STATUS_SCHEDULED);
        }
        jobService.createJob(job);
        return restResponse;
    }

    @GetMapping
    @Operation(summary = "Get all jobs", description = "Returns a paginated list of jobs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = Job.class))
        })
    })
    public Page<Job> getAllJobs(
        @Parameter(description = "Page number") @RequestParam int page,
        @Parameter(description = "Number of items per page") @RequestParam int size,
        @Parameter(description = "Filter by favorite status") @RequestParam(required = false) Boolean favorite,
        @Parameter(description = "Filter by the user id") @RequestParam String createdBy) {
        return jobService.findAll(PageRequest.of(page, size), favorite,createdBy);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a job", description = "Updates the job with the given ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Job updated successfully", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = Job.class))
        }),
        @ApiResponse(responseCode = "404", description = "Job not found", content = @Content)
    })
    public Job updateJob(
        @Parameter(description = "ID of the job to update") @PathVariable Long id,
        @RequestBody @Parameter(description = "Updated job object") Job job) {
        return jobService.updateJob(id, job);
    }
}
