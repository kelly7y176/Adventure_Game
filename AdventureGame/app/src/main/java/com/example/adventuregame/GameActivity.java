package com.example.adventuregame;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class GameActivity extends AppCompatActivity {
    private Game game;
    private MediaPlayer mediaPlayer;
    private ImageView backgroundImage;
    TextView descriptionText;
    Button northButton, southButton, eastButton, westButton, pickupButton, returnButton, defeatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        backgroundImage = findViewById(R.id.background_image);
        descriptionText = findViewById(R.id.description_text);
        northButton = findViewById(R.id.north_button);
        southButton = findViewById(R.id.south_button);
        eastButton = findViewById(R.id.east_button);
        westButton = findViewById(R.id.west_button);
        pickupButton = findViewById(R.id.pickup_button);
        returnButton = findViewById(R.id.return_button);
        defeatButton = findViewById(R.id.defeat_button);

        game = new Game();
        boolean newGame = getIntent().getBooleanExtra("newGame", true);
        if (!newGame) {
            loadGame();
        }
        updateRoom();

        northButton.setOnClickListener(view -> {
            game.moveTo(game.getCurrentRoom().north);
            updateRoom();
            checkBlockedOrDeadEnd(game.getCurrentRoom().north);
        });
        southButton.setOnClickListener(view -> {
            game.moveTo(game.getCurrentRoom().south);
            updateRoom();
            checkBlockedOrDeadEnd(game.getCurrentRoom().south);
        });
        eastButton.setOnClickListener(view -> {
            game.moveTo(game.getCurrentRoom().east);
            updateRoom();
            checkBlockedOrDeadEnd(game.getCurrentRoom().east);
        });
        westButton.setOnClickListener(view -> {
            game.moveTo(game.getCurrentRoom().west);
            updateRoom();
            checkBlockedOrDeadEnd(game.getCurrentRoom().west);
        });
        pickupButton.setOnClickListener(view -> {
            Room currentRoom = game.getCurrentRoom();
            int previousPages = game.getDiaryPages();
            game.pickupItem();
            updateRoom();
            if (game.getDiaryPages() > previousPages) {
                showDiaryMessage(currentRoom.id);
            }
        });
        returnButton.setOnClickListener(view -> {
            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        defeatButton.setOnClickListener(view -> {
            game.defeatEnemy();
            updateRoom();
            if (game.getCurrentRoom().enemy.isEmpty()) {
                Toast.makeText(this, "Sam uses a diary page to banish the Shadow!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You need a page of Sam’s diary to defeat the Shadow!", Toast.LENGTH_SHORT).show();
            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void updateRoom() {
        Room currentRoom = game.getCurrentRoom();
        descriptionText.setText(currentRoom.description);
        int imageId = getResources().getIdentifier(currentRoom.image, "drawable", getPackageName());
        if (imageId == 0) {
            imageId = getResources().getIdentifier("temple", "drawable", getPackageName());
            Toast.makeText(this, "Image missing for " + currentRoom.image + ", using fallback.", Toast.LENGTH_SHORT).show();
        }
        backgroundImage.setImageResource(imageId);

        // Navigation button visibility (text remains black)
        northButton.setVisibility(currentRoom.north != -1 ? View.VISIBLE : View.GONE);
        northButton.setTextColor(0xFF000000); // Always black
        eastButton.setVisibility(currentRoom.east != -1 ? View.VISIBLE : View.GONE);
        eastButton.setTextColor(0xFF000000); // Always black
        southButton.setVisibility(currentRoom.south != -1 ? View.VISIBLE : View.GONE);
        southButton.setTextColor(0xFF000000); // Always black
        westButton.setVisibility(currentRoom.west != -1 ? View.VISIBLE : View.GONE);
        westButton.setTextColor(0xFF000000); // Always black

        // Action button states and text color
        boolean canPickup = !currentRoom.item.isEmpty() && game.getDiaryPages() < 3;
        boolean canDefeat = !currentRoom.enemy.isEmpty() && game.getDiaryPages() > 0;

        pickupButton.setEnabled(canPickup);
        pickupButton.setTextColor(canPickup ? 0xFFFFFF00 : 0xFF000000); // Yellow when available, black otherwise

        defeatButton.setEnabled(canDefeat);
        defeatButton.setTextColor(canDefeat ? 0xFFFFFF00 : 0xFF000000); // Yellow when available, black otherwise

        returnButton.setTextColor(0xFF000000); // Always black

        if (game.getCurrentRoomId() == 18) {
            showEndingDialog();
        }
    }

    private void checkBlockedOrDeadEnd(int attemptedRoomId) {
        Room currentRoom = game.getCurrentRoom();
        if (attemptedRoomId != -1 && game.getCurrentRoomId() != attemptedRoomId && !currentRoom.enemy.isEmpty()) {
            Toast.makeText(this, "Shadow blocks you! Go South for a diary page.", Toast.LENGTH_LONG).show();
        } else if (isDeadEnd(currentRoom)) {
            Toast.makeText(this, "This path leads nowhere. Go South to explore another route.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isDeadEnd(Room room) {
        int exits = 0;
        if (room.north != -1) exits++;
        if (room.south != -1) exits++;
        if (room.east != -1) exits++;
        if (room.west != -1) exits++;
        return exits == 1 && room.enemy.isEmpty() && room.item.isEmpty() && game.getCurrentRoomId() != 18;
    }

    private void showDiaryMessage(int roomId) {
        String memory = "";
        String message = "Sam finds a real adventure—exploring himself, uncovering a new and different Sam.";
        switch (roomId) {
            case 4:
                memory = "Diary Page 1: 'Days blur into monotony at the clerk’s desk, papers piling like a prison.'";
                break;
            case 5:
                memory = "Diary Page 2: 'As a child, I dreamed of jungles and treasures, my heart wild and free.'";
                break;
            case 9:
                memory = "Diary Page 3: 'Before the Ayahuasca, I sat alone, regretting a life unlived.'";
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("A Page of Sam’s Diary");
        builder.setMessage(memory + "\n\n" + message);
        builder.setPositiveButton("Continue", null);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEndingDialog() {
        int pages = game.getDiaryPages();
        if (pages == 3) {
            int imageId = getResources().getIdentifier("empty_tree", "drawable", getPackageName());
            if (imageId != 0) {
                backgroundImage.setImageResource(imageId);
                android.util.Log.d("GameActivity", "Set background to empty_tree");
            } else {
                Toast.makeText(this, "Image missing for empty_tree, keeping current.", Toast.LENGTH_SHORT).show();
                android.util.Log.e("GameActivity", "Failed to load empty_tree");
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Awakening in the Jungle");
        String message;
        if (pages == 3) {
            message = "Sam kneels at the Altar of Awakening, the three pages of his diary glowing before him. " +
                    "The clerk’s monotony, the child’s dreams, the regret—all weave into a tapestry of a life reclaimed. " +
                    "In the dream, he sees his sleeping body beneath the tree, Ayahuasca’s vision fading. " +
                    "Sam has explored himself, found a new and different Sam—an adventurer at last. " +
                    "With a final breath, he wakes to a world where his legend begins.";
            builder.setPositiveButton("Wake Up", (dialog, which) -> finish());
            builder.setCancelable(false);
        } else {
            message = "Sam stands at the Altar of Awakening, but his mind is incomplete. " +
                    "He has found " + pages + " of 3 diary pages. " +
                    "The full truth eludes him—go South to seek the missing pieces of his past.";
            builder.setPositiveButton("Continue", null);
            builder.setCancelable(true);
        }
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveGame() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(openFileOutput("savegame.txt", MODE_PRIVATE));
            writer.write(game.getCurrentRoomId() + "\n" + game.getDiaryPages());
            writer.close();
            Toast.makeText(this, "Sam’s progress saved!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving game!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadGame() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("savegame.txt")));
            int roomId = Integer.parseInt(reader.readLine());
            int pages = Integer.parseInt(reader.readLine());
            game = new Game();
            game.moveTo(roomId);
            for (int i = 0; i < pages; i++) {
                game.pickupItem();
                if (roomId == 4 || roomId == 5 || roomId == 9) {
                    game.getCurrentRoom().item = "";
                }
            }
            reader.close();
            updateRoom();
        } catch (Exception e) {
            Toast.makeText(this, "No saved journey found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}