package com.gamecodeschool.snakeysnakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.SurfaceHolder;

public class GameRenderer {
    private final SurfaceHolder surfaceHolder;
    private final Paint paint;
    private Bitmap backgroundBitmap;
    private Bitmap pauseBitmap;
    private Bitmap playBitmap;

    private final Rect pauseButtonRect;

    private Bitmap mAbilityOneBitmap;
    private final Rect mAbilityOneRect;
    private Bitmap mAbilityTwoBitmap;
    private final Rect mAbilityTwoRect;

    private int score;
    private final Context context;
    private int currentLevel;
    private int scoreForNextLevel;
    private int lives;
    private Bitmap heartBitmap;
    private final Rect heartRect;

    public GameRenderer(Context context, SurfaceHolder surfaceHolder, Point size, int pauseButtonSize) {
        this.surfaceHolder = surfaceHolder;
        this.paint = new Paint();
        this.context = context;
        // Initialize background and pause bitmaps
        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.level1);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, size.x, size.y, false);

        pauseBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pause);
        pauseBitmap = Bitmap.createScaledBitmap(pauseBitmap, pauseButtonSize, pauseButtonSize, false);
        pauseButtonRect = new Rect(20, 20, 20 + pauseButtonSize, 20 + pauseButtonSize);

        playBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.play); // Ensure you have a play.png in your drawables
        playBitmap = Bitmap.createScaledBitmap(playBitmap, pauseButtonSize, pauseButtonSize, false);


        int abilityOneSize = 100;
        mAbilityOneBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ability_one);
        mAbilityOneBitmap = Bitmap.createScaledBitmap(mAbilityOneBitmap, abilityOneSize, abilityOneSize, false);
        mAbilityOneRect = new Rect(20, 150, 20 + abilityOneSize, 150 + abilityOneSize);

        mAbilityTwoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ability_two);
        mAbilityTwoBitmap = Bitmap.createScaledBitmap(mAbilityTwoBitmap, abilityOneSize, abilityOneSize, false);
        mAbilityTwoRect = new Rect(20, 300, 20 + abilityOneSize, 300 + abilityOneSize);

        int heartSize = 50; // Adjust size as needed
        heartBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart);
        heartBitmap = Bitmap.createScaledBitmap(heartBitmap, heartSize, heartSize, false);
        heartRect = new Rect(150, 150, 150 + heartSize, 150 + heartSize); // Position it appropriately


    }

    public void draw(Snake snake, Apple apple, Obstacle obstacle, Portal portal, boolean paused, int score, boolean gameOver, int currentLevel, int scoreForNextLevel, boolean cd_one, boolean cd_two, int lives) {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();

            // Background
            canvas.drawBitmap(backgroundBitmap, 0, 0, null);

            // Pause button
            canvas.drawBitmap(pauseBitmap, null, pauseButtonRect, null);

            // Score
            this.score = score;
            this.currentLevel = currentLevel;
            this.scoreForNextLevel = scoreForNextLevel;
            this.lives=lives;
            drawScore(canvas);

            // Draw snake and apple
            apple.draw(canvas, paint, currentLevel);
            snake.draw(canvas, paint, currentLevel);
            obstacle.draw(canvas, paint, currentLevel);
            if (currentLevel >= 2) {
                portal.draw(canvas, paint, currentLevel);
            }
            if (currentLevel >=2 && cd_one ) {
                canvas.drawBitmap(mAbilityOneBitmap, null, mAbilityOneRect, null);
            }
            if (currentLevel >=3 && cd_two ) {
                canvas.drawBitmap(mAbilityTwoBitmap, null, mAbilityTwoRect, null);
            }
            if (paused) {
                canvas.drawBitmap(playBitmap, null, pauseButtonRect, null);
                drawPauseText(canvas);
            } else {
                canvas.drawBitmap(pauseBitmap, null, pauseButtonRect, null);
            }
            drawLives(canvas, lives);

            if (gameOver) {
                drawGameOverText(canvas);

            }
            gameOver=false;
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
    private void drawLives(Canvas canvas, int lives) {
        // Draw the heart image
        canvas.drawBitmap(heartBitmap, null, heartRect, null);

        // Text setup
        String livesText = "x" + lives;
        float livesTextSize = 65;
        paint.setTextSize(livesTextSize);

        float textX = heartRect.right + 10;
        float textY = heartRect.centerY() + ((livesTextSize / 2) - 10);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        paint.setColor(Color.BLACK);
        canvas.drawText(livesText, textX, textY, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawText(livesText, textX, textY, paint);
    }

    private void drawScore(Canvas canvas) {
        // display the score and level
        String scoreText = "Level " + currentLevel + ": " + score + "/" + scoreForNextLevel;
        float scoreTextSize = 65;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6); //
        paint.setColor(Color.BLACK); // Outline color

        // outline
        paint.setTextSize(scoreTextSize); // Ensure the text size is set for the outline
        float x = 150; // X position of the text
        float y = 100; // Y position of the text
        canvas.drawText(scoreText, x, y, paint);


        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(255, 255, 204, 0)); // gold color

        canvas.drawText(scoreText, x, y, paint);


    }

    private void drawPauseText(Canvas canvas) {
        String nameText = "Huy Dao";
        String secondNameText = "Hormoz Halimi"; // Changed from "Yahir Ramos Perez"
        String thirdNameText = "Muhammad Khawailad Khan"; // Added third name
        String fourthNameText = "Jesse Quach"; // Added fourth name
        float textSize = 60; // Adjust text size as needed

        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));

        // Calculate positions for names
        float nameXPosition = canvas.getWidth() - paint.measureText(nameText) - 20;
        float secondNameXPosition = canvas.getWidth() - paint.measureText(secondNameText) - 20;
        float thirdNameXPosition = canvas.getWidth() - paint.measureText(thirdNameText) - 20;
        float fourthNameXPosition = canvas.getWidth() - paint.measureText(fourthNameText) - 20;
        float topMargin = 50; // Adjust top margin as needed

        // Draw names with fill
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE); // Choose a color that stands out
        canvas.drawText(nameText, nameXPosition, topMargin, paint);
        canvas.drawText(secondNameText, secondNameXPosition, topMargin + textSize + 10, paint);
        canvas.drawText(thirdNameText, thirdNameXPosition, topMargin + (textSize * 2) + 20, paint);
        canvas.drawText(fourthNameText, fourthNameXPosition, topMargin + (textSize * 3) + 30, paint);

        // Draw names with stroke (outline)
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2); // Set the width of the outline
        paint.setColor(Color.BLACK); // Outline color
        canvas.drawText(nameText, nameXPosition, topMargin, paint);
        canvas.drawText(secondNameText, secondNameXPosition, topMargin + textSize + 10, paint);
        canvas.drawText(thirdNameText, thirdNameXPosition, topMargin + (textSize * 2) + 20, paint);
        canvas.drawText(fourthNameText, fourthNameXPosition, topMargin + (textSize * 3) + 30, paint);

        // Draw "Tap to Play" message
        paint.reset(); // Reset the paint to clear previous styles
        paint.setColor(Color.argb(255, 255, 255, 255));
        paint.setTextSize(250);
        if(score==0) {
            String tapToPlayText = context.getResources().getString(R.string.tap_to_play); // Access resources for text
            canvas.drawText(tapToPlayText, 200, 700, paint); // Adjust position as needed
        }
        else
        {
            String tapToRestart = context.getResources().getString(R.string.tap_to_restart); // Access resources for text
            canvas.drawText(tapToRestart, 200, 700, paint); // Adjust position as needed
        }
    }

    private void drawGameOverText(Canvas canvas) {
        // Draw "Game Over" message
        paint.reset(); // Reset the paint to clear previous styles
        paint.setColor(Color.argb(255, 255, 255, 255));
        paint.setTextSize(100);
        String gameOverText = context.getResources().getString(R.string.game_over); // Access resources for text
        canvas.drawText(gameOverText, 200, 500, paint); // Adjust position as needed

    }
    public void setBackgroundBitmap(Bitmap newBackground) {
        backgroundBitmap = newBackground;
    }

}
