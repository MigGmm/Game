package com.game.miguel.game.thread;

import com.game.miguel.game.util.ConstantStorage;
import com.game.miguel.game.view.GameView;

import java.util.Random;

/**
 * Thread who is making enemies while the game is running.
 */
public class EnemyMaker extends Thread {

    private static boolean running = false;

    private Random random = new Random();
    private int difficulty;

    public EnemyMaker(String difficulty) {
        switch (difficulty) {
            case "0":
                this.difficulty = ConstantStorage.EASY;
                break;
            case "1":
                this.difficulty = ConstantStorage.NORMAL;
                break;
            case "2":
                this.difficulty = ConstantStorage.HARD;
                break;
            default:
                this.difficulty = ConstantStorage.NORMAL;
                break;
        }
    }

    public static void setRunning (boolean run) { running = run; }

    @Override
    public void run() {
        while (running) {
            long sleep = random.nextInt(difficulty);
            GameView.makeEnemy();
            try {
                sleep(sleep);
            } catch (InterruptedException e) {
            }
        }
    }
}
