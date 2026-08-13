package com.hotel.packcheck.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CartFloorStateService {

    private final Map<String, Integer> lastKnownFloors =
            new ConcurrentHashMap<>();

    public boolean hasFloorChanged(
            String cartId,
            Integer floor) {

        if (floor == null) {
            return false;
        }

        Integer previousFloor =
                lastKnownFloors.get(cartId);

        if (floor.equals(previousFloor)) {
            return false;
        }

        lastKnownFloors.put(cartId, floor);

        return true;
    }
}