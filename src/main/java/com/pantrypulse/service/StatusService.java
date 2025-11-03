package com.pantrypulse.service;

import com.pantrypulse.model.Status;
import com.pantrypulse.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusService {
    private final StatusRepository repo;
    public List<Status> all() { return repo.findAll(); }
    public Status save(Status s){ return repo.save(s); }
}
