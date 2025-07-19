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

    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    public Page<Job> findAll(PageRequest pageable, Boolean favorite) {
        Specification<Job> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (favorite != null) {
                predicates.add(criteriaBuilder.equal(root.get("favorite"), favorite));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return jobRepository.findAll(spec, pageable);
    }


    public List<Job> findByStatusAndScheduledTimeBefore(String status, LocalDateTime time) {
        return jobRepository.findByStatusAndScheduledTimeBefore(status, time);
    }

    public List<Job> findByTag(boolean favorite) {
        return jobRepository.findByFavorite(favorite);
    }

    public Job updateJob(Long id, Job job) {
        Job existingJob = getJobById(id);
        if (existingJob != null) {
            if (job.isFavorite() != null) {

                existingJob.setFavorite(job.isFavorite());
            }
            if (job.getStatus() != null) {
                existingJob.setStatus(job.getStatus());
            }
            return jobRepository.save(existingJob);
        }
        return null;
    }
}