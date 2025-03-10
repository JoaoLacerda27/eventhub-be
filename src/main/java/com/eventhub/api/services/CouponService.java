package com.eventhub.api.services;

import com.eventhub.api.domain.coupon.Coupon;
import com.eventhub.api.domain.coupon.CouponRequestDTO;
import com.eventhub.api.domain.event.Event;
import com.eventhub.api.repositories.CouponRepository;
import com.eventhub.api.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EventRepository eventRepository;

    public Coupon addCouponToEvent(UUID eventId, CouponRequestDTO request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Coupon coupon = new Coupon();

        coupon.setCode(request.code());
        coupon.setDiscount(request.discount());
        coupon.setValid(new Date(String.valueOf(coupon.getValid())));
        coupon.setEvent(event);

        return couponRepository.save(coupon);
    }

    public List<Coupon> consultCoupons(UUID eventId, Date currentDate) {
        return couponRepository.findByEventIdAndValidAfter(eventId, currentDate);
    }
}
