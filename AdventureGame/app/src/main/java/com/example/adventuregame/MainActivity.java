package com.example.adventuregame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("newGame", true);
            startActivity(intent);
        });
    }
}