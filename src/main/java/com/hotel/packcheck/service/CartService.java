package com.hotel.packcheck.service;

import com.hotel.packcheck.dto.CartCountResponse;
import com.hotel.packcheck.dto.CartResponse;
import com.hotel.packcheck.entity.Cart;
import com.hotel.packcheck.entity.Hotel;
import com.hotel.packcheck.mqtt.MqttService;
import com.hotel.packcheck.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.hotel.packcheck.enums.CartMode;
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final MqttService mqttService;
    public CartService(CartRepository cartRepository,MqttService mqttService) {
        this.cartRepository = cartRepository;
        this.mqttService = mqttService;
    }

    @Transactional
    public CartResponse addCart(String cartId, Hotel hotel) {

        if (cartRepository.existsByCartIdAndHotelHotelId(
                cartId,
                hotel.getHotelId())) {

            throw new IllegalArgumentException(
                    "Cart is already registered with this hotel."
            );
        }

        Cart cart = new Cart();

        cart.setCartId(cartId);
        cart.setHotel(hotel);

        Cart savedCart = cartRepository.save(cart);

        return new CartResponse(
                savedCart.getCartRecordId(),
                savedCart.getCartId(),
                savedCart.getMode()
        );
    }

    @Transactional(readOnly = true)
    public List<CartResponse> getCartsByHotel(Long hotelId) {

        return cartRepository
                .findByHotelHotelId(hotelId)
                .stream()
                .map(cart -> new CartResponse(
                        cart.getCartRecordId(),
                        cart.getCartId(),
                        cart.getMode()
                ))
                .toList();
    }
    @Transactional
    public void removeCart(String cartId, Long hotelId) {

        Cart cart = cartRepository
                .findByCartIdAndHotelHotelId(cartId, hotelId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cart not found for this hotel."
                        ));

        cartRepository.delete(cart);
    }
    @Transactional(readOnly = true)
    public CartCountResponse getCartCounts(Long hotelId) {

        long activeCarts =
                cartRepository.countByHotelHotelIdAndMode(
                        hotelId,
                        CartMode.ACTIVE
                );

        long maintenanceCarts =
                cartRepository.countByHotelHotelIdAndMode(
                        hotelId,
                        CartMode.MAINTENANCE
                );

        return new CartCountResponse(
                activeCarts,
                maintenanceCarts
        );
    }
    @Transactional
    public void updateCartMode(
            String cartId,
            Long hotelId,
            CartMode mode) {

        Cart cart = cartRepository
                .findByCartIdAndHotelHotelId(cartId, hotelId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cart not found for this hotel."
                        ));

        cart.setMode(mode);

        cartRepository.save(cart);

        mqttService.publishModeUpdate(
                cartId,
                mode
        );
    }

}