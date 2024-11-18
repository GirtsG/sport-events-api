package com.sportevents.api.service;

import com.sportevents.api.dto.SportEventDto;
import com.sportevents.api.exception.ResourceNotFoundException;
import com.sportevents.api.model.EventStatus;
import com.sportevents.api.model.SportEvent;
import com.sportevents.api.repository.SportEventRepository;
import com.sportevents.api.repository.SportTypeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class SportEventService {

    private final SportEventRepository eventRepository;
    private final SportTypeRepository typeRepository;

    public Mono<SportEvent> createEvent(@Valid SportEvent event) {
        log.debug("Validating event start time: {}", event.getStartTime());
        return ensureFutureStartTime(event.getStartTime())
                .doOnSuccess(ignored -> log.debug("Validation passed for start time: {}", event.getStartTime()))
                .then(typeRepository.findById(event.getSportTypeId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid sport type ID: " + event.getSportTypeId()))))
                .flatMap(sportType -> {
                    event.setStatus(EventStatus.INACTIVE);
                    log.debug("Setting event status to INACTIVE for event: {}", event);
                    return eventRepository.save(event);
                })
                .doOnSuccess(savedEvent -> log.info("Successfully created event: {}", savedEvent));
    }


    public Flux<SportEventDto> filterEvents(EventStatus status, Long sportTypeId) {
        return Optional.ofNullable(status)
                .map(stat -> Optional.ofNullable(sportTypeId)
                        .map(typeId -> eventRepository.findByStatusAndSportTypeId(stat, typeId))
                        .orElse(eventRepository.findByStatus(stat)))
                .orElseGet(() -> Optional.ofNullable(sportTypeId)
                        .map(eventRepository::findBySportTypeId)
                        .orElse(eventRepository.findAll()))
                .flatMap(this::mapToDto);
    }

    public Mono<SportEventDto> getEventById(Long id) {
        return eventRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Event with ID %d not found".formatted(id))))
                .flatMap(this::mapToDto);
    }

    public Mono<SportEvent> updateEventStatus(Long id, EventStatus newStatus) {
        return eventRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Event with ID %d not found".formatted(id))))
                .flatMap(event -> validateAndTransitionStatus(event, newStatus)
                        .flatMap(eventRepository::save));
    }

    private Mono<Void> ensureFutureStartTime(LocalDateTime startTime) {
        return startTime.isBefore(LocalDateTime.now())
                ? Mono.error(new IllegalArgumentException("Start time cannot be in the past"))
                : Mono.empty();
    }

    private Mono<SportEvent> validateAndTransitionStatus(SportEvent event, EventStatus newStatus) {
        if (event.getStatus() == newStatus) {
            return Mono.error(new IllegalArgumentException("Cannot update event status: Already %s".formatted(newStatus)));
        }
        if (event.getStatus() == EventStatus.FINISHED) {
            return Mono.error(new IllegalStateException("Cannot update status: Event is already FINISHED"));
        }
        if (event.getStatus() == EventStatus.INACTIVE && newStatus == EventStatus.FINISHED) {
            return Mono.error(new IllegalStateException("Cannot transition from INACTIVE to FINISHED"));
        }
        if (newStatus == EventStatus.ACTIVE && event.getStartTime().isBefore(LocalDateTime.now())) {
            return Mono.error(new IllegalStateException("Cannot activate event: Start time is in the past"));
        }
        event.setStatus(newStatus);
        return Mono.just(event);
    }

    private Mono<SportEventDto> mapToDto(SportEvent event) {
        return typeRepository.findById(event.getSportTypeId())
                .map(sportType -> {
                    SportEventDto dto = new SportEventDto();
                    dto.setId(event.getId());
                    dto.setName(event.getName());
                    dto.setSportType(sportType);
                    dto.setStatus(event.getStatus());
                    dto.setStartTime(event.getStartTime());
                    return dto;
                });
    }
}
