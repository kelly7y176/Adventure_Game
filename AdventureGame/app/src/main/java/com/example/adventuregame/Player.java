package com.example.adventuregame;

public class Player {
    private int currentRoomId;
    private String inventory; // Single item for basic requirement

    public Player(int startRoom) {
        this.currentRoomId = startRoom;
        this.inventory = null;
    }

    public void moveTo(int roomId) { this.currentRoomId = roomId; }
    public void pickupItem(String item) { this.inventory = item; }
    public int getCurrentRoomId() { return currentRoomId; }
    public String getInventory() { return inventory; }
}