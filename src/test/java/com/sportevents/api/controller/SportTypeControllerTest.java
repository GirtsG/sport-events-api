package com.sportevents.api.controller;

import com.sportevents.api.model.SportType;
import com.sportevents.api.repository.SportTypeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@WebFluxTest(SportTypeController.class)
class SportTypeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SportTypeRepository repository;

    @Test
    void createSportType_shouldReturnCreated() {
        SportType sportType = new SportType();
        sportType.setName("Basketball");

        when(repository.save(Mockito.any(SportType.class))).thenReturn(Mono.just(sportType));

        webTestClient.post()
                .uri("/api/sport-types")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sportType)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Basketball");
    }

    @Test
    void getAllSportTypes_shouldReturnList() {
        SportType sportType1 = new SportType();
        sportType1.setName("Basketball");

        SportType sportType2 = new SportType();
        sportType2.setName("Football");

        when(repository.findAll()).thenReturn(Flux.just(sportType1, sportType2));

        webTestClient.get()
                .uri("/api/sport-types")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Basketball")
                .jsonPath("$[1].name").isEqualTo("Football");
    }

    @Test
    void getSportTypeById_whenExists_shouldReturnSportType() {
        SportType sportType = new SportType();
        sportType.setId(1L);
        sportType.setName("Basketball");

        when(repository.findById(1L)).thenReturn(Mono.just(sportType));

        webTestClient.get()
                .uri("/api/sport-types/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Basketball");
    }

    @Test
    void getSportTypeById_whenNotExists_shouldReturnNotFound() {
        when(repository.findById(1L)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/sport-types/1")
                .exchange()
                .expectStatus().isNotFound();
    }
}
