package com.hotel.packcheck.service;

import com.hotel.packcheck.dto.FloorRequest;
import com.hotel.packcheck.dto.FloorResponse;
import com.hotel.packcheck.entity.Floor;
import com.hotel.packcheck.entity.Hotel;
import com.hotel.packcheck.repository.FloorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FloorService {

    private final FloorRepository floorRepository;

    public FloorService(FloorRepository floorRepository) {
        this.floorRepository = floorRepository;
    }

    @Transactional
    public FloorResponse addFloor(
            Hotel hotel,
            FloorRequest request) {

        Long hotelId = hotel.getHotelId();

        if (floorRepository
                .findByHotelHotelIdAndFloorNumber(
                        hotelId,
                        request.getFloorNumber()
                )
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Floor number already exists in this hotel."
            );
        }

        Floor floor = new Floor();

        floor.setHotel(hotel);
        floor.setFloorNumber(
                request.getFloorNumber()
        );

        Floor savedFloor =
                floorRepository.save(floor);

        return new FloorResponse(
                savedFloor.getFloorId(),
                savedFloor.getFloorNumber()
        );
    }

    @Transactional
    public void deleteFloor(
            Hotel hotel,
            int floorNumber) {

        Long hotelId = hotel.getHotelId();

        Floor floor = floorRepository
                .findByHotelHotelIdAndFloorNumber(
                        hotelId,
                        floorNumber
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Floor not found for this hotel."
                        ));

        floorRepository.delete(floor);
    }
}