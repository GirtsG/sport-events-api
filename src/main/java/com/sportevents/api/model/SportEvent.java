package com.sportevents.api.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("sport_events")
public class SportEvent {

    @Id
    private Long id;

    @NotNull(message = "Name must not be null")
    private String name;

    @NotNull(message = "SportTypeId must not be null")
    private Long sportTypeId;

    @NotNull(message = "StartTime must not be null")
    private LocalDateTime startTime;

    private EventStatus status;
}
