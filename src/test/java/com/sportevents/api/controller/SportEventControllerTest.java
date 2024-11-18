package com.sportevents.api.controller;

import com.sportevents.api.dto.SportEventDto;
import com.sportevents.api.model.EventStatus;
import com.sportevents.api.model.SportEvent;
import com.sportevents.api.service.SportEventService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(SportEventController.class)
class SportEventControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SportEventService service;

    @Test
    void whenCreateEvent_withValidData_shouldReturnCreated() {
        SportEvent event = new SportEvent();
        event.setName("Champions League Final");
        event.setSportTypeId(1L);
        event.setStartTime(LocalDateTime.now().plusDays(1));

        when(service.createEvent(Mockito.any(SportEvent.class))).thenReturn(Mono.just(event));

        webTestClient.post()
                .uri("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(event)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Champions League Final");
    }

    @Test
    void whenGetEventById_withExistingId_shouldReturnEvent() {
        SportEventDto eventDto = new SportEventDto();
        eventDto.setId(1L);
        eventDto.setName("Champions League Final");

        when(service.getEventById(1L)).thenReturn(Mono.just(eventDto));

        webTestClient.get()
                .uri("/api/events/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Champions League Final");
    }

    @Test
    void whenUpdateEventStatus_withValidTransition_shouldReturnUpdatedEvent() {
        SportEvent event = new SportEvent();
        event.setId(1L);
        event.setStatus(EventStatus.ACTIVE);

        when(service.updateEventStatus(1L, EventStatus.ACTIVE)).thenReturn(Mono.just(event));

        webTestClient.patch()
                .uri("/api/events/1/status?newStatus=ACTIVE")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    void whenGetAllEvents_shouldReturnListOfEvents() {
        SportEventDto event1 = new SportEventDto();
        event1.setId(1L);
        event1.setName("Event 1");

        SportEventDto event2 = new SportEventDto();
        event2.setId(2L);
        event2.setName("Event 2");

        when(service.filterEvents(null, null)).thenReturn(Flux.fromIterable(List.of(event1, event2)));

        webTestClient.get()
                .uri("/api/events")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Event 1")
                .jsonPath("$[1].name").isEqualTo("Event 2");
    }
}
