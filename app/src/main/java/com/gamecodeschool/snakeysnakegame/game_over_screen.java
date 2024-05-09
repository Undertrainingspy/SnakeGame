package com.gamecodeschool.snakeysnakegame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class game_over_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_over_screen);
        Button restartButton = findViewById(R.id.button_restart);
        Button exitButton = findViewById(R.id.button_exit);
        // Get the game over image view
        ImageView imageView = findViewById(R.id.image_game_over);
// Create an ObjectAnimator to fade in the image
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);
        fadeInAnimator.setDuration(1000); // Set duration in milliseconds
        fadeInAnimator.setInterpolator(new DecelerateInterpolator()); // Set animation interpolator
        fadeInAnimator.start(); // Start the animation
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Restart the game
                finish();
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exit the game
                finish();
            }
        });
    }
}