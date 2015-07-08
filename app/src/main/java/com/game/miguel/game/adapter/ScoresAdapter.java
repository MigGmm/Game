package com.game.miguel.game.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.game.miguel.game.R;
import com.game.miguel.game.model.Score;

import java.util.ArrayList;

/**
 * Class who extends BaseAdapter for show scores.
 */
public class ScoresAdapter extends BaseAdapter {
    private final ArrayList<Score> scores;
    private Context context;

    public ScoresAdapter(Context context, ArrayList<Score> scores) {
        this.context = context;
        this.scores = scores;
    }

    @Override
    public int getCount() {
        return scores.size();
    }

    @Override
    public Object getItem(int position) {
        return scores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.single_score, null);
        }
        TextView scorePosition, name, score;
        scorePosition = (TextView) convertView.findViewById(R.id.tvPosition);
        name = (TextView) convertView.findViewById(R.id.tvNameScores);
        score = (TextView) convertView.findViewById(R.id.tvScoreScores);
        scorePosition.setText(position + 1 + "");
        name.setText(scores.get(position).getNombre());
        score.setText(scores.get(position).getScore() + "");
        return convertView;
    }
}
