package com.sportevents.api.repository;

import com.sportevents.api.model.EventStatus;
import com.sportevents.api.model.SportEvent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SportEventRepository extends ReactiveCrudRepository<SportEvent, Long> {
    Flux<SportEvent> findByStatus(EventStatus status);

    Flux<SportEvent> findBySportTypeId(Long sportTypeId);

    Flux<SportEvent> findByStatusAndSportTypeId(EventStatus status, Long sportTypeId);
}
