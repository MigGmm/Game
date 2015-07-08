package com.game.miguel.game.sprite;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Class for control the background sprite.
 */
public class SpriteBackground {
    private int y = 0;
    private int ySpeed = 8;
    private Bitmap bmp;
    private boolean isBottom = true;

    public SpriteBackground(Bitmap bmp) {
        this.bmp = bmp;
    }

    public int getHight() {
        return bmp.getHeight();
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    private void update() {
        y = y + ySpeed;
    }

    public void onDraw(Canvas canvas) {
        update();
        canvas.drawBitmap(bmp, 0 , y, null);
    }

    public boolean isBottom() {
        return isBottom;
    }

    public void setBottom(boolean bottom) {
        isBottom = bottom;
    }
}
