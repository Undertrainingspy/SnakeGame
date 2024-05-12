package com.gamecodeschool.snakeysnakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import java.util.Random;

class Apple implements GameObject {

    private Point location = new Point();

    private Point mSpawnRange;
    private int mSize;
    private int currentLevel;
    private Bitmap mBitmapApple; //apple bitmap

    //constructor
    Apple(Context context, Point sr, int s) {
        // Make a note of the passed in spawn range
        mSpawnRange = sr;
        // Make a note of the size of an apple
        mSize = s;
        // Hide the apple off-screen until the game starts
        location.x = -10; // Initially placing the apple off-screen

        // Load the image to the bitmap
        mBitmapApple = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);
        // Resize the bitmap
        mBitmapApple = Bitmap.createScaledBitmap(mBitmapApple, s, s, false);
    }

    // This is called every time an apple is eaten
    void spawn() {
        // Choose two random values and place the apple
        Random random = new Random();
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    // Let SnakeGame know where the apple is
    // SnakeGame can share this with the snake
    Point getLocation() {
        return location;
    }

    @Override
    public void draw(Canvas canvas, Paint paint, int currentLevel) {
        // Draw the apple
        canvas.drawBitmap(mBitmapApple, location.x * mSize, location.y * mSize, paint);
    }
}
