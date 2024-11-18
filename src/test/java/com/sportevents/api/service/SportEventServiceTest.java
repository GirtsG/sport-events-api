package com.sportevents.api.service;

import com.sportevents.api.model.EventStatus;
import com.sportevents.api.model.SportEvent;
import com.sportevents.api.model.SportType;
import com.sportevents.api.repository.SportEventRepository;
import com.sportevents.api.repository.SportTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SportEventServiceTest {

    @InjectMocks
    private SportEventService service;

    @Mock
    private SportEventRepository eventRepository;

    @Mock
    private SportTypeRepository typeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEvent_withValidData_shouldReturnSavedEvent() {
        SportEvent event = new SportEvent();
        event.setName("Event 1");
        event.setSportTypeId(1L);
        event.setStartTime(LocalDateTime.now().plusDays(1));

        SportType sportType = new SportType();
        sportType.setId(1L);
        sportType.setName("Basketball");

        when(typeRepository.findById(event.getSportTypeId())).thenReturn(Mono.just(sportType));
        when(eventRepository.save(event)).thenReturn(Mono.just(event));

        StepVerifier.create(service.createEvent(event))
                .assertNext(savedEvent -> assertThat(savedEvent.getName()).isEqualTo("Event 1"))
                .verifyComplete();
    }

    @Test
    void updateEventStatus_withValidTransition_shouldUpdateStatus() {
        SportEvent event = new SportEvent();
        event.setId(1L);
        event.setStatus(EventStatus.INACTIVE);
        event.setStartTime(LocalDateTime.now().plusDays(1));

        when(eventRepository.findById(1L)).thenReturn(Mono.just(event));
        when(eventRepository.save(event)).thenReturn(Mono.just(event));

        StepVerifier.create(service.updateEventStatus(1L, EventStatus.ACTIVE))
                .assertNext(updatedEvent -> assertThat(updatedEvent.getStatus()).isEqualTo(EventStatus.ACTIVE))
                .verifyComplete();
    }

    @Test
    void getEventById_withValidId_shouldReturnEvent() {
        SportEvent event = new SportEvent();
        event.setId(1L);
        event.setName("Event 1");
        event.setSportTypeId(1L);

        SportType sportType = new SportType();
        sportType.setId(1L);
        sportType.setName("Basketball");

        when(eventRepository.findById(1L)).thenReturn(Mono.just(event));
        when(typeRepository.findById(1L)).thenReturn(Mono.just(sportType));

        StepVerifier.create(service.getEventById(1L))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(1L);
                    assertThat(dto.getName()).isEqualTo("Event 1");
                    assertThat(dto.getSportType().getName()).isEqualTo("Basketball");
                })
                .verifyComplete();
    }

    @Test
    void whenFilterEvents_withStatusAndSportType_shouldReturnFilteredEvents() {
        SportEvent event = new SportEvent();
        event.setName("Event 1");
        event.setSportTypeId(1L);
        event.setStatus(EventStatus.INACTIVE);

        SportType sportType = new SportType();
        sportType.setId(1L);
        sportType.setName("Basketball");

        when(eventRepository.findByStatusAndSportTypeId(EventStatus.INACTIVE, 1L)).thenReturn(Flux.just(event));
        when(typeRepository.findById(1L)).thenReturn(Mono.just(sportType));

        StepVerifier.create(service.filterEvents(EventStatus.INACTIVE, 1L))
                .assertNext(dto -> {
                    assertThat(dto.getName()).isEqualTo("Event 1");
                    assertThat(dto.getSportType().getName()).isEqualTo("Basketball");
                })
                .verifyComplete();
    }

    @Test
    void filterEvents_withNoFilters_shouldReturnAllEvents() {
        SportEvent event = new SportEvent();
        event.setName("Event 1");
        event.setSportTypeId(1L);

        when(eventRepository.findAll()).thenReturn(Flux.just(event));
        when(typeRepository.findById(1L)).thenReturn(Mono.just(new SportType()));

        StepVerifier.create(service.filterEvents(null, null))
                .assertNext(dto -> assertThat(dto.getName()).isEqualTo("Event 1"))
                .verifyComplete();
    }

    @Test
    void validateStartTime_withPastDate_shouldThrowException() {
        SportEvent event = new SportEvent();
        event.setName("Invalid Event");
        event.setSportTypeId(1L);
        event.setStartTime(LocalDateTime.now().minusDays(1));

        when(typeRepository.findById(event.getSportTypeId())).thenReturn(Mono.just(new SportType()));

        StepVerifier.create(service.createEvent(event))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Start time cannot be in the past"))
                .verify();

        verifyNoInteractions(eventRepository);
    }

    @Test
    void filterEvents_withOnlyStatus_shouldReturnFilteredByStatus() {
        SportEvent event = new SportEvent();
        event.setId(1L);
        event.setName("Event 1");
        event.setSportTypeId(1L);
        event.setStatus(EventStatus.ACTIVE);

        SportType sportType = new SportType();
        sportType.setId(1L);
        sportType.setName("Basketball");

        when(eventRepository.findByStatus(EventStatus.ACTIVE)).thenReturn(Flux.just(event));
        when(typeRepository.findById(1L)).thenReturn(Mono.just(sportType));

        StepVerifier.create(service.filterEvents(EventStatus.ACTIVE, null))
                .assertNext(dto -> {
                    assertThat(dto.getName()).isEqualTo("Event 1");
                    assertThat(dto.getSportType().getName()).isEqualTo("Basketball");
                })
                .verifyComplete();
    }

    @Test
    void updateEventStatus_withInvalidTransition_shouldThrowException() {
        SportEvent event = new SportEvent();
        event.setStatus(EventStatus.FINISHED);

        when(eventRepository.findById(1L)).thenReturn(Mono.just(event));

        StepVerifier.create(service.updateEventStatus(1L, EventStatus.ACTIVE))
                .expectErrorMatches(ex -> ex instanceof IllegalStateException &&
                        ex.getMessage().equals("Cannot update status: Event is already FINISHED"))
                .verify();
    }

    @Test
    void updateEventStatus_withSameStatus_shouldThrowException() {
        SportEvent event = new SportEvent();
        event.setStatus(EventStatus.ACTIVE);

        when(eventRepository.findById(1L)).thenReturn(Mono.just(event));

        StepVerifier.create(service.updateEventStatus(1L, EventStatus.ACTIVE))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().equals("Cannot update event status: Already ACTIVE"))
                .verify();
    }
}
