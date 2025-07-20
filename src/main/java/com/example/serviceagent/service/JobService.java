package com.example.serviceagent.service;

import com.example.serviceagent.model.Job;
import com.example.serviceagent.repository.JobRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public void createJob(Job job) {
        jobRepository.save(job);
    }

    public Job getJobById(Long id, String createdBy) {
        return jobRepository.findByIdAndCreatedBy(id, createdBy).orElse(null);
    }

    public Page<Job> findAll(PageRequest pageable, Boolean favorite, String createdBy) {
        Specification<Job> spec = (root, _, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (favorite != null) {
                predicates.add(criteriaBuilder.equal(root.get("favorite"), favorite));
            }
            predicates.add(criteriaBuilder.equal(root.get("createdBy"), createdBy));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return jobRepository.findAll(spec, pageable);
    }


    public List<Job> findByStatusAndScheduledTimeBefore(String status, LocalDateTime time) {
        return jobRepository.findByStatusAndScheduledTimeBefore(status, time);
    }


    public Job updateJob(Long id, Job job) {
        Job existingJob = getJobById(id, job.getCreatedBy());
        if (existingJob != null) {
            if (job.isFavorite() != null) {
                existingJob.setFavorite(job.isFavorite());
            }
            if (job.getStatus() != null) {
                existingJob.setStatus(job.getStatus());
            }
            if (job.getExecutionTime() != null) {
                existingJob.setExecutionTime(job.getExecutionTime());
            }
            return jobRepository.save(existingJob);
        }
        return null;
    }
}