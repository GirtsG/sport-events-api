package com.sportevents.api.dto;

import com.sportevents.api.model.EventStatus;
import com.sportevents.api.model.SportType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SportEventDto {
    private Long id;
    private String name;
    private SportType sportType;
    private EventStatus status;
    private LocalDateTime startTime;
}
