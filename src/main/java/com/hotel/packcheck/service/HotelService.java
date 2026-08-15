package com.hotel.packcheck.service;

import com.hotel.packcheck.entity.Hotel;
import com.hotel.packcheck.repository.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Transactional
    public Hotel createHotel(Hotel hotel) {

        return hotelRepository.save(hotel);
    }
    @Transactional
    public Hotel updateHotel(Hotel hotel) {

        return hotelRepository.save(hotel);
    }
    public Hotel getHotelById(Long hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() ->
                        new RuntimeException("Hotel not found.")
                );
    }
}