package com.game.miguel.game.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v4.content.LocalBroadcastManager;
import android.util.FloatMath;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.game.miguel.game.R;
import com.game.miguel.game.activity.MainActivity;
import com.game.miguel.game.sprite.SpritePlayerShot;
import com.game.miguel.game.sprite.SpriteEnemy;
import com.game.miguel.game.sprite.SpriteBackground;
import com.game.miguel.game.sprite.SpritePlayer;
import com.game.miguel.game.thread.GameLoopThread;
import com.game.miguel.game.thread.EnemyMaker;
import com.game.miguel.game.util.ConstantStorage;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/**
 * Class who control the canvas and all the objects printed the canvas.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private final int spShot;
    public static ArrayList<SpriteEnemy> enemies = new ArrayList<>();
    public static SurfaceHolder holder;
    private static Bitmap bmpEnemy;
    private static int width, hight;
    private boolean effects;
    private SparseArray<PointF> mActivePointers = new SparseArray<PointF>();
    private ArrayList<SpritePlayerShot> playerShots = new ArrayList<>();
    private boolean touched, secondTouch;
    private boolean shotSideControl = false;
    private GameLoopThread gameLoopThread;
    private EnemyMaker enemyMaker;
    private SpritePlayer player;
    private Bitmap bmpBackground, bmpInnerCircle, bmpButton, bmpButtonPush, bmpPlayerShot, bmpPlayer;
    private int halfY, halfX, lastAngle, zeroX, zeroY;
    private float dx, dy, xTouched, yTouched, near, angle;
    private boolean enemyMakerCalled = false;
    private int secondTouchX, secondTouchY;
    private int widhtBitmap;
    private int hightBitmap;
    private long lastClick;
    private Paint paint;
    private SpriteBackground background, background1;
    private SoundPool sp;

    public GameView(Context context, String difficulty, boolean effects) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        this.effects = effects;
        gameLoopThread = new GameLoopThread(this);
        enemyMaker = new EnemyMaker(difficulty);
        bmpBackground = BitmapFactory.decodeResource(getResources(), R.drawable.background5);
        bmpInnerCircle = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        bmpEnemy = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
        bmpPlayer = BitmapFactory.decodeResource(getResources(), R.drawable.spitfire);
        bmpPlayerShot = BitmapFactory.decodeResource(getResources(), R.drawable.playershot);
        bmpButton = BitmapFactory.decodeResource(getResources(),
                R.drawable.freebutton);
        bmpButtonPush = BitmapFactory.decodeResource(getResources(),
                R.drawable.pressbutton);
        background = new SpriteBackground(bmpBackground);
        background1 = new SpriteBackground(bmpBackground);
        player = new SpritePlayer(this, bmpPlayer);
        sp = new SoundPool(30, AudioManager.STREAM_MUSIC, 0);
        spShot = sp.load(context, R.raw.shot, 1);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
    }

    /**
     * Create a new enemy and store that into de ArrayList of enemies.
     */
    public static void makeEnemy() {
        try {
            enemies.add(new SpriteEnemy(bmpEnemy, width, hight));
        } catch (ConcurrentModificationException e) {
        }
    }

    /**
     * Delete all enemies of the ArrayList.
     */
    public static void resetEnemies() {
        enemies = new ArrayList<>();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameLoopThread.setRunning(true);
        if (gameLoopThread.getState() != Thread.State.RUNNABLE)
            gameLoopThread.start();
        enemyMaker.setRunning(true);
        Canvas c = holder.lockCanvas(null);
        holder.unlockCanvasAndPost(c);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        gameLoopThread.setRunning(false);
        enemyMaker.setRunning(false);
        while (retry) {
            try {
                gameLoopThread.join();
                enemyMaker.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Draw the canvas with all the objects.
     * @param canvas
     */
    @SuppressLint("WrongCall")
    public void onDraw(Canvas canvas) {
        generateDimensions(canvas);
        if (!enemyMakerCalled) {
            enemyMaker.start();
            enemyMakerCalled = true;
        }
        printBackground(canvas);
        printBaseCircle(canvas);
        canvas.drawText(MainActivity.score + "", width * 8 / 10, hight * 1 / 15, paint);
        player.onDraw(canvas);
        checkJoystick(canvas);
        checkShotButton(canvas);
        checkEdges();
        checkCollisions(canvas);
        for (SpritePlayerShot spritePlayerShot : playerShots) {
            spritePlayerShot.onDraw(canvas);
        }
    }

    private void printBackground(Canvas canvas) {
        if (background.getY() >= 0 && background1.isBottom()){
            background1.setY(0 - background1.getHight());
            background1.setBottom(false);
            background.setBottom(true);
            background.onDraw(canvas);
            background1.onDraw(canvas);
        } else
        if (background1.getY() >= 0 && background.isBottom()) {
            background.setY(0 - background.getHight());
            background1.setBottom(true);
            background.setBottom(false);
            background1.onDraw(canvas);
            background.onDraw(canvas);
        } else {
            background.onDraw(canvas);
            background1.onDraw(canvas);
        }
    }

    /**
     * Check all possibles collisions between the different objects in the canvas. If the player collision
     * with an enemy, the player lose. If an player shot collision with a enemy, this enemy get a life reduction.
     * @param canvas
     */
    private void checkCollisions(Canvas canvas) {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            enemies.get(i).onDraw(canvas);
            if (enemies.get(i).crash(player.getX() + player.getWidth() * 1 / 4, player.getY() + player.getHight() * 1 / 4)
                    || enemies.get(i).crash(player.getX() + player.getWidth() * 1 / 4, player.getY() + player.getHight() * 3 / 4)
                    || enemies.get(i).crash(player.getX() + player.getWidth() * 3 / 4, player.getY() + player.getHight() * 3 / 4)
                    || enemies.get(i).crash(player.getX() + player.getWidth() * 3 / 4, player.getY() + player.getHight() * 1 / 4)) {
                enemyMaker.setRunning(false);
                gameLoopThread.setRunning(false);
                Intent intent = new Intent(ConstantStorage.GAME_OVER);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            }
            for (int y = playerShots.size() - 1; y >= 0; y--) {
                if (enemies.get(i).crash(playerShots.get(y).getX(), playerShots.get(y).getY())) {
                    enemies.get(i).diminishLife();
                    if (enemies.get(i).getLife() <= 0) {
                        enemies.remove(i);
                        MainActivity.addPoints(100);
                    }
                    playerShots.remove(y);
                    break;
                }
            }
        }
    }

    /**
     * Check the position of all objects. Avoid the enemies go out of the screen and destroy the player
     * shots when cross the screen edge.
     */
    private void checkEdges() {
        for (int y = playerShots.size() - 1; y >= 0; y--) {
            if (playerShots.get(y).getY() < 0 - bmpPlayerShot.getHeight())
                playerShots.remove(y);
        }
        for (int i = enemies.size() - 1; i >= 0; i--) {
            if (enemies.get(i).getY() > hight) enemies.remove(i);
        }
    }

    /**
     * Draw the black base circle of the joystick.
     * @param canvas
     */
    private void printBaseCircle(Canvas canvas) {
        Paint pcircle = new Paint();
        pcircle.setColor(Color.BLACK);
        pcircle.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width * 1 / 5, hight * 7 / 8, 80, pcircle);
    }

    /**
     * Get the dimensions of the canvas, who depends of the screen size and resolution.
     * @param canvas
     */
    private void generateDimensions(Canvas canvas) {
        width = canvas.getWidth();
        hight = canvas.getHeight();
        halfX = width / 2;
        halfY = hight / 2;
        zeroX = width * 1 / 5;
        zeroY = hight * 7 / 8;
        dx = xTouched - zeroX;
        dy = yTouched - zeroY;
        widhtBitmap = bmpButton.getWidth();
        hightBitmap = bmpButton.getHeight();
    }

    /**
     * This method control if the player is touching the joystick, and calculate the position of the tap.
     * @param canvas
     */
    private void checkJoystick(Canvas canvas) {
        if (touched || secondTouch) {
            if (touched && (xTouched < halfX && yTouched > halfY)) {
                calculateValues();
                calculatePlayerMovement();
                canvas.drawBitmap(bmpInnerCircle, xTouched - bmpInnerCircle.getWidth() / 2, yTouched - bmpInnerCircle.getHeight() / 2, null);
            } else if (secondTouch && (secondTouchX < halfX && secondTouchY > halfY)) {
                calculateValues();
                calculatePlayerMovement();
                canvas.drawBitmap(bmpInnerCircle, secondTouchX - bmpInnerCircle.getWidth() / 2, secondTouchY - bmpInnerCircle.getHeight() / 2, null);
            } else {
                player.setSpeed(0, 0);
                canvas.drawBitmap(bmpInnerCircle, zeroX - bmpInnerCircle.getWidth() / 2, zeroY - bmpInnerCircle.getHeight() / 2, null);
            }
        } else {
            player.setSpeed(0, 0);
            canvas.drawBitmap(bmpInnerCircle, zeroX - bmpInnerCircle.getWidth() / 2, zeroY - bmpInnerCircle.getHeight() / 2, null);
        }
    }

    /**
     * This method check the taps over the shot button.
     * @param canvas
     */
    private void checkShotButton(Canvas canvas) {
        if (touched || secondTouch) {
            if (touched && (yTouched > hight * 5 / 6 && (yTouched < halfY
                    + (halfY * 3 / 4) + hightBitmap)) && (xTouched > width * 3 / 4)
                    && (xTouched < halfX + (halfX * 3 / 4) + widhtBitmap)) {
                canvas.drawBitmap(bmpButtonPush, width * 3 / 4, hight * 5 / 6, null);
                playerShot();
            } else if (secondTouch && (secondTouchY > hight * 5 / 6 && (secondTouchY < halfY
                    + (halfY * 3 / 4) + hightBitmap)) && (secondTouchX > width * 3 / 4)
                    && (secondTouchX < halfX + (halfX * 3 / 4) + widhtBitmap)) {
                canvas.drawBitmap(bmpButtonPush, width * 3 / 4, hight * 5 / 6, null);
                playerShot();
            } else canvas.drawBitmap(bmpButton, width * 3 / 4, hight * 5 / 6, null);
        } else canvas.drawBitmap(bmpButton, width * 3 / 4, hight * 5 / 6, null);
    }

    /**
     * Generate a player's shot when the player tap on the button. The shot frequency is also controlled.
     */
    private void playerShot() {
        try {
            if (System.currentTimeMillis() - lastClick > 20) {
                lastClick = System.currentTimeMillis();
                if (shotSideControl) {
                    playerShots.add(new SpritePlayerShot(bmpPlayerShot, hight, player.getX() + bmpPlayer.getWidth() * 1 / 8, player.getY()));
                    if (effects) sp.play(spShot, 1.0f, 1.0f, 0, 0, 1.5f);
                    shotSideControl = false;
                } else {
                    playerShots.add(new SpritePlayerShot(bmpPlayerShot, hight, player.getX() + bmpPlayer.getWidth() * 3 / 5, player.getY()));
                    if (effects) sp.play(spShot, 1.0f, 1.0f, 0, 0, 1.5f);
                    shotSideControl = true;
                }
            }
        } catch (ConcurrentModificationException e) {
        }
    }

    /**
     * When the joystick is tapped, this method calculate the angle where the joystick is and set
     * the player speed.
     */
    private void calculatePlayerMovement() {
        int x = 0;
        int y = 0;
        if (lastAngle >= -45 && lastAngle < 45) {
            x = 0;
            y = -getPower();
        } else if (lastAngle >= 45 && lastAngle < 75) {
            x = getPower();
            y = -getPower();
        } else if (lastAngle >= 75 && lastAngle < 105) {
            x = getPower();
            y = 0;
        } else if (lastAngle >= 105 && lastAngle < 135) {
            x = getPower();
            y = getPower();
        } else if (lastAngle >= 135 || lastAngle < -135) {
            x = 0;
            y = getPower();
        } else if (lastAngle >= -135 && lastAngle < -105) {
            x = -getPower();
            y = getPower();
        } else if (lastAngle >= -105 && lastAngle < -75) {
            x = -getPower();
            y = 0;
        } else if (lastAngle >= -75 && lastAngle < -44) {
            x = -getPower();
            y = -getPower();
        }
        player.setSpeed(x, y);
    }

    /**
     * This method control the player touches.
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        int maskedAction = event.getActionMasked();
        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
                xTouched = (int) event.getX(pointerIndex);
                yTouched = (int) event.getY(pointerIndex);
                touched = true;
            case MotionEvent.ACTION_UP:
                touched = false;
            case MotionEvent.ACTION_CANCEL: {
                touched = false;
                break;
            }
            case MotionEvent.ACTION_OUTSIDE:
                touched = false;
            case MotionEvent.ACTION_POINTER_DOWN: {
                PointF f = new PointF();
                secondTouchX = (int) event.getX(pointerIndex);
                secondTouchY = (int) event.getY(pointerIndex);
                mActivePointers.put(pointerId, f);
                secondTouch = true;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                secondTouch = false;
            case MotionEvent.ACTION_MOVE: {
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    PointF point = mActivePointers.get(event.getPointerId(i));
                    if (point != null) {
                        point.x = event.getX(i);
                        point.y = event.getY(i);
                    }
                }
                xTouched = (int) event.getX(pointerIndex);
                yTouched = (int) event.getY(pointerIndex);
                touched = true;
                break;
            }
        }
        invalidate();
        return true;
    }

    /**
     * This method calculate the angle and the distant where the joystick is from the center of the joystick.
     * Set the position, x and y, where the played tapped.
     */
    private void calculateValues() {
        dx = xTouched - zeroX;
        dy = yTouched - zeroY;
        getAngle();
        angle = (float) Math.atan(Math.abs(dy / dx));
        near = FloatMath.sqrt(dx * dx + dy * dy);
        if (near > ConstantStorage.RADIUS) {
            if (dx > 0 && dy > 0) {
                xTouched = (zeroX + (ConstantStorage.RADIUS * FloatMath.cos(angle)));
                yTouched = (zeroY + (ConstantStorage.RADIUS * FloatMath.sin(angle)));
            } else if (dx > 0 && dy < 0) {
                xTouched = (zeroX + (ConstantStorage.RADIUS * FloatMath.cos(angle)));
                yTouched = (zeroY - (ConstantStorage.RADIUS * FloatMath.sin(angle)));
            } else if (dx < 0 && dy < 0) {
                xTouched = (zeroX - (ConstantStorage.RADIUS * FloatMath.cos(angle)));
                yTouched = (zeroY - (ConstantStorage.RADIUS * FloatMath.sin(angle)));
            } else if (dx < 0 && dy > 0) {
                xTouched = (zeroX - (ConstantStorage.RADIUS * FloatMath.cos(angle)));
                yTouched = (zeroY + (ConstantStorage.RADIUS * FloatMath.sin(angle)));
            }
        } else {
            xTouched = zeroX + dx;
            yTouched = zeroY + dy;
        }
    }

    /**
     * Return the angle between the tapped point and the center of the joystick.
     * @return
     */
    private int getAngle() {
        if (xTouched > zeroX) {
            if (yTouched < zeroY) {
                return lastAngle = (int) (Math.atan((yTouched - zeroY)
                        / (xTouched - zeroX))
                        * ConstantStorage.RADIUS + 90);
            } else if (yTouched > zeroY) {
                return lastAngle = (int) (Math.atan((yTouched - zeroY)
                        / (xTouched - zeroX)) * ConstantStorage.RADIUS) + 90;
            } else {
                return lastAngle = 90;
            }
        } else if (xTouched < zeroX) {
            if (yTouched < zeroY) {
                return lastAngle = (int) (Math.atan((yTouched - zeroY)
                        / (xTouched - zeroX))
                        * ConstantStorage.RADIUS - 90);
            } else if (yTouched > zeroY) {
                return lastAngle = (int) (Math.atan((yTouched - zeroY)
                        / (xTouched - zeroX)) * ConstantStorage.RADIUS) - 90;
            } else {
                return lastAngle = -90;
            }
        } else {
            if (yTouched <= zeroY) {
                return lastAngle = 0;
            } else {
                if (lastAngle < 0) {
                    return lastAngle = -180;
                } else {
                    return lastAngle = 180;
                }
            }
        }
    }

    /**
     * Calculate the power of the player move in relation to the distance between the center of the
     * joystick and the point tapped.
     * @return
     */
    private int getPower() {
        return (int) (15 * Math.sqrt((xTouched - zeroX)
                * (xTouched - zeroX) + (yTouched - zeroY)
                * (yTouched - zeroY)) / ConstantStorage.RADIUS);
    }
}
