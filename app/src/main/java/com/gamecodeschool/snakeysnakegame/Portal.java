package com.gamecodeschool.snakeysnakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class Portal implements GameObject, Movable {
    private Point location;
    private Bitmap portalBitmap;
    private int blockSize;
    private int currentLevel;
    // Constructor
    public Portal(Context context, Point gameSize, int blockSize) {
        this.blockSize = blockSize;
        portalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.portalgif);
        portalBitmap = Bitmap.createScaledBitmap(portalBitmap, blockSize, blockSize, false);
        location = new Point(-1, -1); // not on the screen
    }

    public void spawn(Point newLocation) {
        location = newLocation;
    }

    public Point getLocation() {
        return location;
    }

    // draw method from GameObject interface
    @Override
    public void draw(Canvas canvas, Paint paint, int currentLevel) {
        if (location.x != -1 && location.y != -1) {
            canvas.drawBitmap(portalBitmap, location.x * blockSize, location.y * blockSize, paint);
        }
    }

    // deactivate the portal
    public void deactivate() {
        location = new Point(-1, -1); // Remove from screen
    }

    // move method from Movable interface
    @Override
    public void move() {
    }
}

