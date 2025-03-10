package com.eventhub.api.controllers;

import com.eventhub.api.domain.event.Event;
import com.eventhub.api.domain.event.EventDetailsDTO;
import com.eventhub.api.domain.event.EventRequestDTO;
import com.eventhub.api.domain.event.EventResponseDTO;
import com.eventhub.api.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Event> create(@RequestParam("title") String title,
                                        @RequestParam(value = "description", required = false) String description,
                                        @RequestParam("date") Long date,
                                        @RequestParam("city") String city,
                                        @RequestParam("state") String state,
                                        @RequestParam("remote") Boolean remote,
                                        @RequestParam("eventUrl") String eventUrl,
                                        @RequestParam(value = "image", required = false) MultipartFile image) {
        EventRequestDTO eventRequestDTO = new EventRequestDTO(
                title, description, date, city, state, remote, eventUrl, image);

        Event newEvent = this.eventService.createEvent(eventRequestDTO);
        return ResponseEntity.ok(newEvent);
    }

    @GetMapping
    public Page<EventResponseDTO> getAllUpcomingEvents(Pageable pageable) {
        return this.eventService.getAllUpcomingEvents(pageable);
    }

    @GetMapping("/filter")
    public Page<EventResponseDTO> filterEvents(@RequestParam(required = false) String title,
                                               @RequestParam(required = false) String city,
                                               @RequestParam(required = false) String uf,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
                                               Pageable pageable) {
        return this.eventService.getFilteredEvents(title, city, uf, startDate, endDate, pageable);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDetailsDTO> getEventDetails(@PathVariable UUID eventId) {
        EventDetailsDTO eventDetails = eventService.getEventDetails(eventId);
        return ResponseEntity.ok(eventDetails);
    }
}
