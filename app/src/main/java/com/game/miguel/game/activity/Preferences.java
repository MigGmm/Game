package com.game.miguel.game.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.game.miguel.game.R;

/**
 * Activity with the preferences.
 */
public class Preferences extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
