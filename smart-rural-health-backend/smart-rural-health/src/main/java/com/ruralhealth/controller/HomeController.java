package com.ruralhealth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Smart Rural Health Backend Running Successfully";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
