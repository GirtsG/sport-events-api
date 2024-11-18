package com.sportevents.api.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("sport_types")
public class SportType {

    @Id
    private Long id;

    @NotNull(message = "Name must not be null")
    private String name;
}
