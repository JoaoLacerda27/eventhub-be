package com.eventhub.api.controllers;

import com.eventhub.api.domain.event.Event;
import com.eventhub.api.domain.event.EventRequestDTO;
import com.eventhub.api.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<Event> create(@RequestBody EventRequestDTO request) {
        Event newEvent = this.eventService.createEvent(request);
        return ResponseEntity.ok(newEvent);
    }
}
