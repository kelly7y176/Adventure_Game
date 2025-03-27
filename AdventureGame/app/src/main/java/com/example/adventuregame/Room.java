package com.example.adventuregame;

public class Room {
    int id;
    String description;
    int north, south, east, west;
    String image;
    String item;
    String enemy;

    public Room(int id, String description, int north, int south, int east, int west, String image, String item, String enemy) {
        this.id = id;
        this.description = description;
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
        this.image = image;
        this.item = item;
        this.enemy = enemy;
    }
}