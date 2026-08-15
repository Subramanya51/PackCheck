package com.hotel.packcheck.service;

import com.hotel.packcheck.dto.HotelConfigurationRequest;
import com.hotel.packcheck.dto.HotelDetailsUpdateRequest;
import com.hotel.packcheck.entity.Admin;
import com.hotel.packcheck.entity.Floor;
import com.hotel.packcheck.entity.Hotel;
import com.hotel.packcheck.repository.AdminRepository;
import com.hotel.packcheck.repository.FloorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import com.hotel.packcheck.dto.HotelConfigurationResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
@Service
public class HotelConfigurationService {

    private final HotelService hotelService;
    private final AdminRepository adminRepository;
    private final FloorRepository floorRepository;
    private final PasswordEncoder passwordEncoder;
    public HotelConfigurationService(
            HotelService hotelService,
            AdminRepository adminRepository,
            FloorRepository floorRepository,
            PasswordEncoder passwordEncoder) {

        this.hotelService = hotelService;
        this.adminRepository = adminRepository;
        this.floorRepository = floorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public HotelConfigurationResponse configureHotel(
            HotelConfigurationRequest request) {
        if (request.getNumberOfFloors() <= 0) {
            throw new IllegalArgumentException(
                    "Number of floors must be greater than zero."
            );
        }
        if (adminRepository.existsByEmail(request.getAdminEmail())) {
            throw new IllegalArgumentException(
                    "An admin with this email already exists."
            );
        }
        Hotel hotel = new Hotel();

        hotel.setHotelName(request.getHotelName());
        hotel.setCountry(request.getCountry());
        hotel.setState(request.getState());
        hotel.setCity(request.getCity());
        hotel.setPostalCode(request.getPostalCode());
        hotel.setNumberOfFloors(request.getNumberOfFloors());
        Hotel savedHotel =
                hotelService.createHotel(hotel);

        Admin admin = new Admin();

        admin.setName(request.getAdminName());
        admin.setEmail(request.getAdminEmail());
        admin.setPassword(passwordEncoder.encode(request.getAdminPassword()));
        admin.setHotel(savedHotel);

        adminRepository.save(admin);

        for (int floorNumber = 1;
             floorNumber <= request.getNumberOfFloors();
             floorNumber++) {

            Floor floor = new Floor();

            floor.setFloorNumber(floorNumber);
            floor.setHotel(savedHotel);

            floorRepository.save(floor);
        }

        return new HotelConfigurationResponse(
                savedHotel.getHotelId(),
                savedHotel.getHotelName(),
                savedHotel.getCountry(),
                savedHotel.getState(),
                savedHotel.getCity(),
                savedHotel.getPostalCode()
        );
    }
    @Transactional
    public HotelConfigurationResponse updateHotelDetails(
            Long hotelId,
            HotelDetailsUpdateRequest request) {

        Hotel hotel = hotelService.getHotelById(hotelId);

        hotel.setHotelName(request.getHotelName());
        hotel.setCity(request.getCity());
        hotel.setState(request.getState());
        hotel.setCountry(request.getCountry());
        hotel.setPostalCode(request.getPostalCode());

        Hotel updatedHotel = hotelService.updateHotel(hotel);

        return new HotelConfigurationResponse(
                updatedHotel.getHotelId(),
                updatedHotel.getHotelName(),
                updatedHotel.getCountry(),
                updatedHotel.getState(),
                updatedHotel.getCity(),
                updatedHotel.getPostalCode()
        );
    }
}