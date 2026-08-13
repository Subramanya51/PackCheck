package com.hotel.packcheck.dto;

public class TaskRequest {

    private String cartId;
    private int requestedFloor;
    private String requestedRoom;

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public int getRequestedFloor() {
        return requestedFloor;
    }

    public void setRequestedFloor(int requestedFloor) {
        this.requestedFloor = requestedFloor;
    }

    public String getRequestedRoom() {
        return requestedRoom;
    }

    public void setRequestedRoom(String requestedRoom) {
        this.requestedRoom = requestedRoom;
    }
}