package com.gamecodeschool.snakeysnakegame;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface GameObject {  //interface for draw
    void draw(Canvas canvas, Paint paint, int currentLevel);

}

