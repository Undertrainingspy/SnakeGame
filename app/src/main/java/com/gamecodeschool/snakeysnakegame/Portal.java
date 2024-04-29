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

    // Constructor
    public Portal(Context context, Point gameSize, int blockSize) {
        this.blockSize = blockSize;
        portalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.portalgif);
        portalBitmap = Bitmap.createScaledBitmap(portalBitmap, blockSize, blockSize, false);
        location = new Point(-1, -1); // Initially not on the screen
    }

    // Method to spawn the portal at a specific location
    public void spawn(Point newLocation) {
        location = newLocation;
    }

    // Method to get the location of the portal
    public Point getLocation() {
        return location;
    }

    // Implement draw method from GameObject interface
    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (location.x != -1 && location.y != -1) {
            canvas.drawBitmap(portalBitmap, location.x * blockSize, location.y * blockSize, paint);
        }
    }

    // Method to deactivate (remove) the portal
    public void deactivate() {
        location = new Point(-1, -1); // Remove from screen
    }

    // Implement move method from Movable interface
    @Override
    public void move() {
        // Logic to move the portal if necessary; possibly to a new random location
        // For example, to move randomly every few seconds or triggered by an event
        // Currently, this does not do anything but can be extended to have actual move logic
    }
}
