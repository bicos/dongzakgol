package com.pockru.dongzakgol;

import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by rhpark on 2016. 1. 21..
 * JIRA: MWP-
 */
public class SettingActivity extends PreferenceActivity {

    public static final String KEY_VERSION_NAME = "version_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting_preference);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Toolbar bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.view_title, root, false);
            root.addView(bar, 0); // insert at top
        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);

            root.removeAllViews();

            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.view_title, root, false);

            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }else{
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);

            root.addView(content);
            root.addView(bar);
        }

        bar.setTitle("설정");
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Preference version = findPreference(KEY_VERSION_NAME);
        version.setSummary(BuildConfig.VERSION_NAME);

    }
}
