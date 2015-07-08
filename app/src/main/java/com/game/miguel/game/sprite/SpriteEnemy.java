package com.game.miguel.game.sprite;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * class for control the enemy sprites.
 */
public class SpriteEnemy {

    private int life;
    private int x, y, screenWidth, high;
    private int xSpeed = 0;
    private int ySpeed = 6;
    private Bitmap bmp;
    private Random random = new Random();

    public SpriteEnemy(Bitmap bmp, int ancho, int high) {
        life = 10;
        x = 0;
        y = 0;
        this.screenWidth = ancho;
        this.high = high;
        this.bmp=bmp;
        setInitialPosition();
        setxSpeed();
    }

    public boolean crash(int x2, int y2) {
        return x2 > x && x2 < x + bmp.getWidth() && y2 > y && y2 < y + bmp.getHeight();
    }

    public void diminishLife() {
        life = life - 1;
    }

    public int getLife() {
        return life;
    }

    private void setInitialPosition() {
        x = random.nextInt(screenWidth - bmp.getWidth());
    }

    private void setxSpeed() {
        int aux = random.nextInt(4);
        switch (aux) {
            case 0:
                xSpeed = - 8;
                break;
            case 1:
                xSpeed = - 4;
                break;
            case 2:
                xSpeed = 0;
                break;
            case 3:
                xSpeed = 4;
                break;
            case 4:
                xSpeed = 8;
                break;
        }
    }

    private void update() {
        if (x > screenWidth - bmp.getWidth() - xSpeed) {
            xSpeed = xSpeed * - 1;
        }
        if (x + xSpeed < 0) {
            xSpeed = xSpeed * - 1;
        }
        y = y + ySpeed;
        x = x + xSpeed;
    }

    public int getY () {
        return y;
    }
    public int getX () {
        return x;
    }

    public void onDraw(Canvas canvas) {
        update();
        canvas.drawBitmap(bmp, x , y, null);
    }
}
