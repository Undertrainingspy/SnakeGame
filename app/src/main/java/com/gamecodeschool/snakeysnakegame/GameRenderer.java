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
    private int score;
    private final Context context;
    public GameRenderer(Context context, SurfaceHolder surfaceHolder, Point size,
                        int pauseButtonSize) {
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


    }

    public void draw(Snake snake, Apple apple,Obstacle obstacle, boolean paused, int score) {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();

            // Background
            canvas.drawBitmap(backgroundBitmap, 0, 0, null);

            // Pause button
            canvas.drawBitmap(pauseBitmap, null, pauseButtonRect, null);

            // Score
            this.score = score;
            drawScore(canvas);

            // Draw snake and apple
            apple.draw(canvas, paint);
            snake.draw(canvas, paint);
            obstacle.draw(canvas, paint);
            if (paused) {
                canvas.drawBitmap(playBitmap, null, pauseButtonRect, null);
                drawPauseText(canvas);
            } else {
                canvas.drawBitmap(pauseBitmap, null, pauseButtonRect, null);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawScore(Canvas canvas) {
        String scoreText = "" + score;
        float scoreTextSize = 120;

        // Configure paint for outline
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8); // Set the width of the outline
        paint.setColor(Color.BLACK); // Outline color

        // Draw the outline
        paint.setTextSize(scoreTextSize); // Ensure the text size is set for outline
        canvas.drawText(scoreText, 120, 120, paint);

        // Configure paint for text fill
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(230, 105, 165, 255)); // Text fill color

        // Draw the text fill, which will be inside the outline
        canvas.drawText(scoreText, 120, 120, paint);
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


}
