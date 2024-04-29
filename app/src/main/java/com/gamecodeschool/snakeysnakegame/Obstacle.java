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
    private int goblinDirection;


    Obstacle(Context context, Point sr, int s) {
        mSpawnRange = sr;
        mSize = s;
        location.x = -10;
        mBitmapGoblin = BitmapFactory.decodeResource(context.getResources(), R.drawable.goblin);
        mBitmapGoblin = Bitmap.createScaledBitmap(mBitmapGoblin, s, s, false);
    }

    void spawn() {
        Random random = new Random();
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    // Based on 0-4, randomly move the goblin obstacle
    @Override
     public void move() {
        Random random = new Random();
        goblinDirection = random.nextInt(4);

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

    Point getLocation() {
        return location;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapGoblin, location.x * mSize, location.y * mSize, paint);
    }
}
