package com.pockru.dongzakgol;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.webkit.WebView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 래형 on 2015-12-24.
 */
public class DzkApplication extends Application {

    private Tracker mTracker;

    public static AtomicBoolean initCateList = new AtomicBoolean(false);

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(Const.TRACKER_ID);

            // Provide unhandled exceptions reports. Do that first after creating the tracker
            mTracker.enableExceptionReporting(true);

            // Enable Remarketing, Demographics & Interests reports
            // https://developers.google.com/analytics/devguides/collection/android/display-features
            mTracker.enableAdvertisingIdCollection(true);

            // Enable automatic activity tracking for your app
            mTracker.enableAutoActivityTracking(true);
        }
        return mTracker;
    }
}
