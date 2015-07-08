package com.game.miguel.game.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Window;

import com.game.miguel.game.R;
import com.game.miguel.game.thread.GameLoopThread;
import com.game.miguel.game.thread.EnemyMaker;
import com.game.miguel.game.util.ConstantStorage;
import com.game.miguel.game.view.GameView;

/**
 * This activity load the game view.
 */
public class GameActivity extends Activity {

    private GameView gameView;
    private String difficulty;
    private MediaPlayer mp;
    private boolean effects;
    private boolean music;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mp = MediaPlayer.create(this, R.raw.gaming);
        mp.setLooping(true);
        loadPreferences();
        gameView = new GameView(this, difficulty, effects);
        setContentView(gameView);
        LocalBroadcastManager.getInstance(this).registerReceiver(new MessageHandler(),
                new IntentFilter(ConstantStorage.GAME_OVER));
    }

    @Override
    public void onBackPressed() {
        if (music) mp.stop();
        GameView.resetEnemies();
        EnemyMaker.setRunning(false);
        GameLoopThread.setRunning(false);
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (music) mp.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (music) mp.stop();
    }

    /**
     * Get the preferences.
     */
    private void loadPreferences() {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        difficulty = pref.getString(ConstantStorage.DIFFICULTY, "1");
        music = pref.getBoolean(ConstantStorage.MUSIC, true);
        effects = pref.getBoolean(ConstantStorage.EFFECTS, true);
        if (music) mp.start();
    }

    /**
     * Finnish the game.
     */
    public void gameOver() {
        if (music) mp.stop();
        setResult(RESULT_OK);
        finish();
    }

    /**
     * Class used for the communication between GameView and this activity.
     */
    public class MessageHandler extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            gameOver();
        }
    }
}
