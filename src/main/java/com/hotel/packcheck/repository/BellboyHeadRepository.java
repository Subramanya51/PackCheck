package com.hotel.packcheck.repository;

import com.hotel.packcheck.entity.BellboyHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BellboyHeadRepository
        extends JpaRepository<BellboyHead, Long> {





    Optional<BellboyHead> findByLoginId(String loginId);

    @Query("""
            SELECT b
            FROM BellboyHead b
            JOIN FETCH b.hotel
            WHERE b.loginId = :loginId
            """)
    Optional<BellboyHead> findByLoginIdWithHotel(String loginId);
    Optional<BellboyHead> findByHotelHotelId(Long hotelId);
}