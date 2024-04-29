package com.gamecodeschool.snakeysnakegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.graphics.ImageDecoder;
import android.graphics.ImageDecoder.Source;

public class Portal implements GameObject, Movable {
    private Point location;
    private Drawable portalDrawable;
    private int blockSize;
    private int portalSize; // Significantly smaller size for the portal

    // Constructor
    public Portal(Context context, Point gameSize, int blockSize) {
        this.blockSize = blockSize;
        this.portalSize = blockSize / 100; // Making the portal very small, adjust this value as needed

        // Calculate the exact bottom left position
        int xPos = 0; // x-coordinate at the left edge
        int yPos = gameSize.y - blockSize; // y-coordinate at the bottom, ensuring it's at the last block row

        location = new Point(xPos / blockSize, yPos / blockSize); // Set location in terms of block coordinates

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                Source source = ImageDecoder.createSource(context.getResources(), R.drawable.portalgif);
                portalDrawable = ImageDecoder.decodeDrawable(source);
                if (portalDrawable instanceof AnimatedImageDrawable) {
                    ((AnimatedImageDrawable) portalDrawable).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        if (location.x != -1 && location.y != -1 && portalDrawable != null) {
            int left = location.x * blockSize; // Calculate left position based on block size
            int top = location.y * blockSize; // Calculate top position based on block size
            portalDrawable.setBounds(left, top, left + portalSize, top + portalSize);
            portalDrawable.draw(canvas);
        }
    }

    // Method to deactivate (remove) the portal
    public void deactivate() {
        location = new Point(-1, -1); // Remove from screen
    }

    @Override
    public void move() {
    }
}
