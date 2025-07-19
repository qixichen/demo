package com.example.serviceagent.model;

import jakarta.persistence.*;

@Entity
public class HeaderInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "\"key\"")
    private String key;
    @Column(name = "\"value\"")
    private String value;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public HeaderInfo(String value, String key) {
        this.value = value;
        this.key = key;
    }

    public HeaderInfo() {
    }
}