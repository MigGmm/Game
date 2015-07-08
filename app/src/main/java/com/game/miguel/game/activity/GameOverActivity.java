package com.game.miguel.game.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.game.miguel.game.R;
import com.game.miguel.game.database.ControlSQLite;
import com.game.miguel.game.model.Score;
import com.game.miguel.game.util.ConstantStorage;

/**
 * Activity who is called when the game is over. Show the score of the player. The player can store that
 * in the database.
 */
public class GameOverActivity extends Activity {

    private TextView tvScore;
    private EditText etName;
    private MediaPlayer mp;
    private boolean music;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_over_layout);
        etName = (EditText) findViewById(R.id.etName);
        tvScore = (TextView) findViewById(R.id.tvScore);
        tvScore.setText(MainActivity.score + "");
        mp = MediaPlayer.create(this, R.raw.gameover);
        mp.setLooping(true);
        checkMusic();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (music) mp.start();
    }

    @Override
    public void onStart() {
        super.onRestart();
        if (music) mp.start();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (music) mp.stop();
    }

    @Override
    public void onBackPressed() {
    }

    /**
     * Check if the music preference is on.
     */
    private void checkMusic() {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        music = pref.getBoolean(ConstantStorage.MUSIC, true);
        if (music) mp.start();

    }

    /**
     * Insert the player score into th database.
     * @param v
     */
    public void insertScore(View v) {
        ControlSQLite db = new ControlSQLite(this);
        db.insertScore(new Score(etName.getText().toString(), MainActivity.score));
        db.close();
        if (music) mp.stop();
        MainActivity.score = 0;
        finish();
    }

    /**
     * Reset the score and finnish.
     * @param v
     */
    public void noInsertScores(View v) {
        if (music) mp.stop();
        MainActivity.score = 0;
        finish();
    }
}
