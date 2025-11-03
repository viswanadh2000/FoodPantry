package com.pantrypulse.controller;

import com.pantrypulse.model.Status;
import com.pantrypulse.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/status")
@RequiredArgsConstructor
public class StatusController {
    private final StatusService service;

    @GetMapping
    public List<Status> all(){ return service.all(); }

    @PostMapping
    public Status create(@RequestBody Status s){ return service.save(s); }
}
