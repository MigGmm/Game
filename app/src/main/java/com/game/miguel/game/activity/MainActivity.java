package com.game.miguel.game.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;

import com.game.miguel.game.R;
import com.game.miguel.game.util.ConstantStorage;
import com.game.miguel.game.view.GameView;

public class MainActivity extends Activity {

    public static int score = 0;
    private MediaPlayer mp;
    private boolean music;

    public static void addPoints(int puntos) {
        score = score + puntos;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_layout);
        mp = MediaPlayer.create(this, R.raw.menu);
        mp.setLooping(true);
        checkMusic();
        score = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkMusic();
    }

    @Override
    public void onStart() {
        super.onRestart();
        checkMusic();
    }


    @Override
    public void onPause() {
        super.onPause();
        checkMusic();
        mp.stop();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(this, GameOverActivity.class);
            startActivity(intent);
            GameView.resetEnemies();
        }
        if (resultCode == RESULT_CANCELED) score = 0;
    }

    public void closeApp(View v) {
        finish();
    }

    /**
     * Check if the music preference is on.
     */
    private void checkMusic() {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        music = pref.getBoolean(ConstantStorage.MUSIC, true);
        if (music) {
            mp.start();
        }
    }

    public void launchGame(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivityForResult(intent, ConstantStorage.GAME_ACTIVITY_CODE);
        if (music) mp.stop();

    }

    public void launchScores(View v) {
        Intent intent = new Intent(this, ScoresActivity.class);
        startActivity(intent);
    }

    public void launchPreferences(View v) {
        Intent intent = new Intent(this, Preferences.class);
        startActivity(intent);
    }

    public void launchAbout(View v) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}
