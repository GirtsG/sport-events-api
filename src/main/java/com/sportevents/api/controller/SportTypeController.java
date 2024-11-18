package com.sportevents.api.controller;

import com.sportevents.api.model.SportType;
import com.sportevents.api.repository.SportTypeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sport-types")
public class SportTypeController {

    private final SportTypeRepository repository;

    @PostMapping
    public Mono<ResponseEntity<SportType>> createSportType(@Valid @RequestBody SportType sportType) {
        log.info("Received request to create sport type: {}", sportType);
        return repository.save(sportType)
                .map(savedType -> ResponseEntity.status(HttpStatus.CREATED).body(savedType));
    }

    @GetMapping
    public Flux<SportType> getAllSportTypes() {
        log.info("Fetching all sport types");
        return repository.findAll()
                .doOnComplete(() -> log.info("Fetched all sport types successfully"))
                .doOnError(ex -> log.error("Failed to fetch sport types", ex));
    }


    @GetMapping("/{id}")
    public Mono<ResponseEntity<SportType>> getSportTypeById(@PathVariable Long id) {
        log.info("Fetching sport type with ID: {}", id);
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
