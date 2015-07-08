package com.game.miguel.game.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.game.miguel.game.R;
import com.game.miguel.game.adapter.ScoresAdapter;
import com.game.miguel.game.database.ControlSQLite;
import com.game.miguel.game.model.Score;

import java.util.ArrayList;

/**
 * Created by miguel on 10/02/15.
 */
public class ScoresActivity extends Activity {

    private ArrayList<Score> scores = new ArrayList<>();
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.score_layout);
        listView = (ListView) findViewById(R.id.lvScores);
        scores = cargaPuntuaciones();
        listView.setAdapter(new ScoresAdapter(this, scores));
    }

    private ArrayList<Score> cargaPuntuaciones() {
        ControlSQLite bd = new ControlSQLite(this);
        return bd.listScores();
    }
}
