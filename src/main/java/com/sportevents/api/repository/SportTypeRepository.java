package com.sportevents.api.repository;

import com.sportevents.api.model.SportType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SportTypeRepository extends ReactiveCrudRepository<SportType, Long> {
}
