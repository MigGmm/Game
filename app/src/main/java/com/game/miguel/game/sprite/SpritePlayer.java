package com.game.miguel.game.sprite;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.game.miguel.game.view.GameView;

/**
 * Class for control player sprite.
 */
public class SpritePlayer {
    private int x, y;
    private int xSpeed = 0;
    private int ySpeed = 0;
    private GameView gameView;
    private Bitmap bmp;

    public SpritePlayer(GameView gameView, Bitmap bmp) {
        this.gameView=gameView;
        this.bmp=bmp;
        x = 300;
        y = 800;
    }

    public void setSpeed(int x, int y) {
        this.xSpeed = x;
        this.ySpeed = y;
    }

    public int getX () {
        return x;
    }

    public int getY () {
        return y;
    }

    public int getHight() {return bmp.getHeight(); }

    public int getWidth() { return bmp.getWidth(); }

    private void update() {
        if (x > gameView.getWidth() - bmp.getWidth() - xSpeed) {
            xSpeed = 0;
        }
        if (x + xSpeed < 0) {
            xSpeed = 0;
        }
        if (y > gameView.getHeight() - bmp.getHeight() - ySpeed) {
            ySpeed = 0;
        }
        if (y + ySpeed< 0) {
            ySpeed = 0;
        }
        y = y + ySpeed;
        x = x + xSpeed;
    }

    public void onDraw(Canvas canvas) {
        update();
        canvas.drawBitmap(bmp, x , y, null);
    }
}
