package com.hotel.packcheck.repository;

import com.hotel.packcheck.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    Optional<Hotel> findByHotelName(String hotelName);
}