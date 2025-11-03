package com.pantrypulse.controller;

import com.pantrypulse.model.SystemEvent;
import com.pantrypulse.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventsController {
    
    private final EventService eventService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<SystemEvent>> streamEvents() {
        return eventService.getEventStream()
                .map(event -> ServerSentEvent.<SystemEvent>builder()
                        .id(event.getTimestamp().toString())
                        .event(event.getEventType())
                        .data(event)
                        .build())
                .mergeWith(
                    // Send heartbeat every 30 seconds to keep connection alive
                    Flux.interval(Duration.ofSeconds(30))
                        .map(seq -> ServerSentEvent.<SystemEvent>builder()
                                .event("heartbeat")
                                .comment("keepalive")
                                .build())
                );
    }
}
