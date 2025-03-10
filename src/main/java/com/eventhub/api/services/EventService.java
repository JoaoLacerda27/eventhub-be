package com.eventhub.api.services;

import com.eventhub.api.config.AWS.service.BucketService;
import com.eventhub.api.domain.address.Address;
import com.eventhub.api.domain.coupon.Coupon;
import com.eventhub.api.domain.event.Event;
import com.eventhub.api.domain.event.EventDetailsDTO;
import com.eventhub.api.domain.event.EventRequestDTO;
import com.eventhub.api.domain.event.EventResponseDTO;
import com.eventhub.api.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    @Autowired
    private EventRepository repository;

    @Autowired
    private BucketService bucketService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CouponService couponService;

    public Event createEvent(EventRequestDTO data) {
        String imgUrl = "";

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

        if (!data.remote()) {
            this.addressService.createAddress(data, newEvent);
        }

        return newEvent;
    }

    public Page<EventResponseDTO> getAllUpcomingEvents(Pageable pageable) {
        Page<Event> eventsPage = this.repository.findUpcomingEvents(new Date(), pageable);

        return eventsPage.map(event -> new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddress() != null ? event.getAddress().getCity() : "",
                event.getAddress() != null ? event.getAddress().getUf() : "",
                event.getRemote(),
                event.getEventUrl(),
                event.getImgUrl()
        ));
    }

    public Page<EventResponseDTO> getFilteredEvents(String title, String city, String uf, Date startDate, Date endDate, Pageable pageable) {
        title = (title != null) ? title : "";
        city = (city != null) ? city : "";
        uf = (uf != null) ? uf : "";
        startDate = (startDate != null) ? startDate : new Date(0);
        endDate = (endDate != null) ? endDate : new Date();

        Page<Event> eventPage = this.repository.findFilteredEvents(new Date(), title, city, uf, startDate, endDate, pageable);

        return eventPage.map(event -> new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddress() != null ? event.getAddress().getCity() : "",
                event.getAddress() != null ? event.getAddress().getUf() : "",
                event.getRemote(),
                event.getEventUrl(),
                event.getImgUrl()
        ));
    }

    public EventDetailsDTO getEventDetails(UUID eventId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Optional<Address> address = addressService.findByEventId(eventId);

        List<Coupon> coupons = couponService.consultCoupons(eventId, new Date());

        List<EventDetailsDTO.CouponDTO> couponDTOs = coupons.stream()
                .map(coupon -> new EventDetailsDTO.CouponDTO(
                        coupon.getCode(),
                        coupon.getDiscount(),
                        coupon.getValid()))
                .collect(Collectors.toList());

        return new EventDetailsDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                address.isPresent() ? address.get().getCity() : "",
                address.isPresent() ? address.get().getUf() : "",
                event.getImgUrl(),
                event.getEventUrl(),
                couponDTOs);
    }

}
