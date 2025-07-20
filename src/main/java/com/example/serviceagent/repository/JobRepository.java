package com.example.serviceagent.repository;

import com.example.serviceagent.model.Job;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    List<Job> findByStatusAndScheduledTimeBefore(String status, LocalDateTime time);

    @Override
    Page<Job> findAll(Specification<Job> spec, Pageable pageable);

    Optional<Job> findByIdAndCreatedBy(Long id, String createdBy);

}