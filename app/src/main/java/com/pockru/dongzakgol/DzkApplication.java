package com.pockru.dongzakgol;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.webkit.WebView;

/**
 * Created by 래형 on 2015-12-24.
 */
public class DzkApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().putString(SettingActivity.KEY_VERSION_NAME, BuildConfig.VERSION_NAME).apply();
    }
}
