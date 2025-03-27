package com.example.adventuregame;

import java.util.HashMap;
import java.util.Map;

public class Game {
    private Map<Integer, Room> rooms;
    private int currentRoomId;
    private int diaryPages; // Tracks number of diary pages (0-3)

    public Game() {
        rooms = new HashMap<>();
        loadRooms();
        currentRoomId = 1;
        diaryPages = 0;
    }

    private void loadRooms() {
        rooms.put(1, new Room(1, "You wake in a humid jungle clearing, mind foggy from Ayahuasca.", 2, 4, -1, -1, "jungle_clearing", "", ""));
        rooms.put(2, new Room(2, "A crumbling stone path winds through vines.", 3, -1, -1, 1, "stone_path", "", ""));
        rooms.put(3, new Room(3, "A fork splits the path into three directions.", -1, 5, 6, 2, "fork", "", ""));
        rooms.put(4, new Room(4, "A dark cave echoes with dripping water.", 7, -1, -1, 1, "cave", "diary_page_1", ""));
        rooms.put(5, new Room(5, "A rushing river cuts through the jungle.", 8, -1, -1, 3, "river", "diary_page_2", ""));
        rooms.put(6, new Room(6, "Ancient ruins crumble under thick vines.", 9, -1, -1, 3, "ruins", "", ""));
        rooms.put(7, new Room(7, "A shrine glows faintly, guarded by a Shadow.", 10, 4, -1, -1, "shrine", "", "shadow"));
        rooms.put(8, new Room(8, "A rickety bridge sways over a gorge.", 11, 5, -1, -1, "bridge", "", ""));
        rooms.put(9, new Room(9, "A stone statue looms, clutching a page.", 12, 6, -1, -1, "statue", "diary_page_3", ""));
        rooms.put(10, new Room(10, "A deep pit yawns before you.", 13, 7, -1, -1, "pit", "", ""));
        rooms.put(11, new Room(11, "Cliffs rise steeply, a Shadow lurking.", 14, 8, -1, -1, "cliffs", "", "shadow"));
        rooms.put(12, new Room(12, "A hidden garden blooms with strange flowers.", 15, 9, -1, -1, "garden", "", ""));
        rooms.put(13, new Room(13, "A vault of stone holds forgotten secrets.", 15, 10, -1, -1, "vault", "", ""));
        rooms.put(14, new Room(14, "A chasm splits the earth, barely passable.", 15, 11, -1, -1, "chasm", "", ""));
        rooms.put(15, new Room(15, "You enter the Temple of Truth, its walls pulsing with light.", 16, 13, -1, 12, "temple_entrance", "", ""));
        rooms.put(16, new Room(16, "In the Hall of Reflections, Sam sees his true body sleeping beneath a tree.", 17, 15, -1, -1, "hall_reflections", "", ""));
        rooms.put(17, new Room(17, "The Chamber of Dreams reveals this is all a vision—Ayahuasca’s gift.", 18, 16, -1, -1, "chamber_dreams", "", ""));
        rooms.put(18, new Room(18, "At the Altar of Awakening, Sam finds himself—whole, ready to wake.", -1, 17, -1, -1, "altar_awakening", "", ""));
    }

    public Room getCurrentRoom() {
        return rooms.get(currentRoomId);
    }

    public void moveTo(int newRoomId) {
        if (newRoomId != -1) {
            Room currentRoom = rooms.get(currentRoomId);
            if (!currentRoom.enemy.isEmpty() && diaryPages == 0 &&
                    (newRoomId == currentRoom.north || newRoomId == currentRoom.east)) {
                return; // Blocked by enemy if no diary pages
            }
            currentRoomId = newRoomId;
        }
    }

    public void pickupItem() {
        Room currentRoom = rooms.get(currentRoomId);
        if (!currentRoom.item.isEmpty() && diaryPages < 3) {
            diaryPages++;
            currentRoom.item = ""; // Remove the item after pickup
        }
    }

    public void defeatEnemy() {
        Room currentRoom = rooms.get(currentRoomId);
        if (!currentRoom.enemy.isEmpty() && diaryPages > 0) {
            currentRoom.enemy = "";
        }
    }

    public int getCurrentRoomId() { return currentRoomId; }
    public int getDiaryPages() { return diaryPages; }
}