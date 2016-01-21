package com.pockru.dongzakgol;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Created by rhpark on 2016. 1. 21..
 * JIRA: MWP-
 */
public class SettingActivity extends PreferenceActivity{

    public static final String KEY_VERSION_NAME = "version_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting_preference);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Preference version = findPreference(KEY_VERSION_NAME);
        version.setSummary(BuildConfig.VERSION_NAME);

    }
}
