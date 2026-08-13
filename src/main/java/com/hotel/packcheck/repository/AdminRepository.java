package com.hotel.packcheck.repository;

import com.hotel.packcheck.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            SELECT a
            FROM Admin a
            JOIN FETCH a.hotel
            WHERE a.email = :email
            """)
    Optional<Admin> findByEmailWithHotel(String email);
}