package com.eventhub.api.controllers;

import com.eventhub.api.domain.coupon.Coupon;
import com.eventhub.api.domain.coupon.CouponRequestDTO;
import com.eventhub.api.services.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/event/{eventId}")
    public ResponseEntity<Coupon> addCouponsToEvent(@PathVariable UUID eventId, @RequestBody CouponRequestDTO request) {
        Coupon coupon = couponService.addCouponToEvent(eventId, request);
        return ResponseEntity.ok(coupon);
    }
}
