package com.game.miguel.game.sprite;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Class for control shot sprites.
 */
public class SpritePlayerShot {

    private int x, y, hight;
    private int ySpeed = - 25;
    private Bitmap bmp;

    public SpritePlayerShot(Bitmap bmp, int hight, int x, int y) {
        this.hight = hight;
        this.bmp=bmp;
        this.x = x;
        this.y = y;
    }

    public int getX () {
        return x + bmp.getWidth() / 2;
    }

    public int getY () {
        return y + bmp.getHeight() / 2;
    }

    private void update() {
        y = y + ySpeed;
    }

    public void onDraw(Canvas canvas) {
        update();
        canvas.drawBitmap(bmp, x , y, null);
    }
}
