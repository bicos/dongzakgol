package com.pockru.dongzakgol;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.webkit.WebView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by 래형 on 2015-12-24.
 */
public class DzkApplication extends Application {

    private Tracker mTracker;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().putString(SettingActivity.KEY_VERSION_NAME, BuildConfig.VERSION_NAME).apply();
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);

            // Provide unhandled exceptions reports. Do that first after creating the tracker
            mTracker.enableExceptionReporting(true);
        }
        return mTracker;
    }
}
