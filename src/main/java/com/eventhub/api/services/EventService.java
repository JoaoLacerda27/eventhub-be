package com.eventhub.api.services;

import com.eventhub.api.config.AWS.service.BucketService;
import com.eventhub.api.domain.event.Event;
import com.eventhub.api.domain.event.EventRequestDTO;
import com.eventhub.api.domain.event.EventResponseDTO;
import com.eventhub.api.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class EventService {

    @Autowired
    private EventRepository repository;

    @Autowired
    private BucketService bucketService;

    public Event createEvent(EventRequestDTO data) {
        String imgUrl = null;

        if (data.image() != null) {
            imgUrl = bucketService.uploadImg(data.image());
        }

        Event newEvent = new Event();
        newEvent.setTitle(data.title());
        newEvent.setDescription(data.description());
        newEvent.setEventUrl(data.eventUrl());
        newEvent.setDate(new Date(data.date()));
        newEvent.setImgUrl(imgUrl);
        newEvent.setRemote(data.remote());

        repository.save(newEvent);

        return newEvent;
    }

    public Page<EventResponseDTO> getAllUpcomingEvent(Pageable pageable) {
        Page<Event> eventsPage = this.repository.findUpcomingEvents(new Date(), pageable);

        return eventsPage.map(event -> new EventResponseDTO(
                event.getId(), event.getTitle(), event.getDescription(), event.getDate(), "", "", event.getRemote(), event.getEventUrl(), event.getImgUrl()
        ));
    }

}
