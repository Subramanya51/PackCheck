package com.hotel.packcheck.service;

import com.google.api.core.ApiFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FirebaseRealtimeService {

    private final DatabaseReference cartStatusReference;

    public FirebaseRealtimeService(FirebaseApp firebaseApp) {

        FirebaseDatabase firebaseDatabase =
                FirebaseDatabase.getInstance(firebaseApp);

        this.cartStatusReference =
                firebaseDatabase.getReference("cartStatus");
    }

    public ApiFuture<Void> updateCartStatus(
            String cartId,
            Map<String, Object> status) {

        return cartStatusReference
                .child(cartId)
                .setValueAsync(status);
    }
}