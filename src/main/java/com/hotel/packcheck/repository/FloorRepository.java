package com.hotel.packcheck.repository;

import com.hotel.packcheck.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FloorRepository extends JpaRepository<Floor, Long> {

    List<Floor> findByHotelHotelId(Long hotelId);

    Optional<Floor> findByFloorNumberAndHotelHotelId(
            int floorNumber,
            Long hotelId
    );

    boolean existsByFloorNumberAndHotelHotelId(
            int floorNumber,
            Long hotelId
    );

    Optional<Floor> findByHotelHotelIdAndBssid1(
            Long hotelId,
            String bssid
    );

    Optional<Floor> findByHotelHotelIdAndBssid2(
            Long hotelId,
            String bssid
    );
    Optional<Floor> findByHotelHotelIdAndFloorNumber(
            Long hotelId,
            int floorNumber
    );

}