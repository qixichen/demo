package com.example.serviceagent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock/api")
public class MockAPIController {

    @GetMapping
    public String hello() {
        return "hello";
    }
}
