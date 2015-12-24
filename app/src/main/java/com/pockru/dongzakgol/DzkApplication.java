package com.pockru.dongzakgol;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.webkit.WebView;

/**
 * Created by 래형 on 2015-12-24.
 */
public class DzkApplication extends Application {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }
}
