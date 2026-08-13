package com.hotel.packcheck.service;

import com.hotel.packcheck.dto.TaskRequest;
import com.hotel.packcheck.entity.Hotel;
import com.hotel.packcheck.repository.CartRepository;
import com.hotel.packcheck.mqtt.MqttService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final MqttService mqttService;
    private final CartRepository cartRepository;

    @Transactional(readOnly = true)
    public void sendTaskRequest(
            TaskRequest request,
            Hotel hotel) {

        Long hotelId = hotel.getHotelId();

        cartRepository
                .findByCartIdAndHotelHotelId(
                        request.getCartId(),
                        hotelId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cart not found for this hotel."
                        ));

        mqttService.publishTaskRequest(
                request.getCartId(),
                request.getRequestedFloor(),
                request.getRequestedRoom()
        );
    }
}
//package com.hotel.packcheck.service;
//
//import com.hotel.packcheck.dto.TaskRequest;
//import com.hotel.packcheck.mqtt.MqttService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class TaskService {
//
//    private final MqttService mqttService;
//
//    public void sendTaskRequest(TaskRequest request) {
//
//        mqttService.publishTaskRequest(
//                request.getCartId(),
//                request.getRequestedFloor(),
//                request.getRequestedRoom()
//        );
//    }
//}