package com.gamecodeschool.snakeysnakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import java.util.Random;

class Obstacle implements GameObject, Movable {

    private Point location = new Point();
    private Point mSpawnRange;
    private int mSize;
    private Bitmap mBitmapGoblin;


    // apple to obstacle distance
    private int appleLocationX;
    private int appleLocationY;
    private int directionToApple;
    private int randomDirection;
    private Point mAppleLocation;


    Obstacle(Context context, Point sr, int s, Point appleLocation) {
        mSpawnRange = sr;
        mSize = s;
        location.x = -10;
        mBitmapGoblin = BitmapFactory.decodeResource(context.getResources(), R.drawable.goblin);
        mBitmapGoblin = Bitmap.createScaledBitmap(mBitmapGoblin, s, s, false);
        mAppleLocation = appleLocation;
    }

    void spawn() {
        Random random = new Random();
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    // calculates distance from goblin to apple then moves horizontal/vertical depending on apple
    private int distanceToApple() {
        appleLocationX = mAppleLocation.x - location.x;
        appleLocationY = mAppleLocation.y - location.y;

        if (appleLocationX != 0) {
            if (appleLocationX > 0)
                return 1; // moves right
            else
                return 3; // left
        }
        else {
            if (appleLocationY > 0)
                return 2; // down
            else
                return 0; // up
        }
    }

    // Based on 0-4, randomly move the goblin obstacle
    private void goblinMovement(int goblinDirection) {
        switch (goblinDirection) {
            case 0: // move goblin up
                if (location.y > 0) {
                    location.y--;
                }
                break;
            case 1: // move goblin right
                if (location.x < mSpawnRange.x - 1) {
                    location.x++;
                }
                break;
            case 2: // move goblin down
                if (location.y < mSpawnRange.y - 1) {
                    location.y++;
                }
                break;
            case 3: // move goblin left
                if (location.x > 0) {
                    location.x--;
                }
                break;
        }
    }

    // goblin movement can either move towards the apple or move a random direction
    @Override
     public void move() {
        Random random = new Random();
        boolean isMoveToApple = random.nextBoolean();

        if (isMoveToApple) {
            directionToApple = distanceToApple();
            goblinMovement(directionToApple);
        }
        else {
            randomDirection = random.nextInt(4);
            goblinMovement(randomDirection);
        }
    }

    Point getLocation() {
        return location;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapGoblin, location.x * mSize, location.y * mSize, paint);
    }
}
