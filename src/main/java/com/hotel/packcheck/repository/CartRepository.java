package com.hotel.packcheck.repository;

import com.hotel.packcheck.entity.Cart;
import com.hotel.packcheck.enums.CartMode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository
        extends JpaRepository<Cart, Long> {

    List<Cart> findByHotelHotelId(Long hotelId);

    Optional<Cart> findByCartIdAndHotelHotelId(
            String cartId,
            Long hotelId
    );

    boolean existsByCartIdAndHotelHotelId(
            String cartId,
            Long hotelId
    );
    Optional<Cart> findByCartId(String cartId);
    long countByHotelHotelIdAndMode(
            Long hotelId,
            CartMode mode
    );
    List<Cart> findAllByHotelHotelId(Long hotelId);

}