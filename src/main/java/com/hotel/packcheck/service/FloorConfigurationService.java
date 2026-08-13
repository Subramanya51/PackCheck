package com.hotel.packcheck.service;

import com.hotel.packcheck.entity.Floor;
import com.hotel.packcheck.repository.FloorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FloorConfigurationService {

    private final FloorRepository floorRepository;

    public FloorConfigurationService(FloorRepository floorRepository) {
        this.floorRepository = floorRepository;
    }

    @Transactional
    public Floor configureBssid(
            Long floorId,
            String bssid1,
            String bssid2) {

        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Floor not found."
                        ));

        floor.setBssid1(bssid1);
        floor.setBssid2(bssid2);

        return floorRepository.save(floor);
    }
    @Transactional(readOnly = true)
    public Integer getFloorForBssid(
            Long hotelId,
            String bssid) {

        return floorRepository
                .findByHotelHotelIdAndBssid1(hotelId, bssid)
                .or(() ->
                        floorRepository
                                .findByHotelHotelIdAndBssid2(
                                        hotelId,
                                        bssid
                                )
                )
                .map(Floor::getFloorNumber)
                .orElse(null);
    }
}