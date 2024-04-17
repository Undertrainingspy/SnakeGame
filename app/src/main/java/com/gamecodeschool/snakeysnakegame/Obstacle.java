package com.gamecodeschool.snakeysnakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import java.util.Random;

class Obstacle implements GameObject {

    private Point location = new Point();
    private Point mSpawnRange;
    private int mSize;
    private Bitmap mBitmapGoblin;

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

    Point getLocation() {
        return location;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapGoblin, location.x * mSize, location.y * mSize, paint);
    }
}
