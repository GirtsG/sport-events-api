package com.sportevents.api.controller;

import com.sportevents.api.dto.SportEventDto;
import com.sportevents.api.model.EventStatus;
import com.sportevents.api.model.SportEvent;
import com.sportevents.api.service.SportEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/events")
public class SportEventController {

    private final SportEventService service;

    @PostMapping
    public Mono<ResponseEntity<SportEvent>> createEvent(@Valid @RequestBody SportEvent event) {
        log.info("Received request to create event: {}", event);
        return service.createEvent(event)
                .doOnSuccess(savedEvent -> log.info("Successfully created event with ID: {}", savedEvent.getId()))
                .map(savedEvent -> ResponseEntity.status(HttpStatus.CREATED).body(savedEvent));
    }

    @GetMapping
    public Flux<SportEventDto> getAllEvents(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) Long sportTypeId) {
        log.info("Fetching events with status={} and sportTypeId={}", status, sportTypeId);
        return service.filterEvents(status, sportTypeId);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<SportEventDto>> getEventById(@PathVariable Long id) {
        log.info("Fetching event with ID: {}", id);
        return service.getEventById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public Mono<ResponseEntity<SportEvent>> updateEventStatus(
            @PathVariable Long id,
            @RequestParam EventStatus newStatus) {
        log.info("Updating event status for ID: {} to {}", id, newStatus);
        return service.updateEventStatus(id, newStatus)
                .map(ResponseEntity::ok);
    }
}
