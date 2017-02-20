package com.pockru.dongzakgol;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.ValueCallback;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by 래형 on 2015-12-24.
 */
public class BaseActivity extends AppCompatActivity {

    // onActivityResult값
    public static final int REQ_FILECHOOSER = 102;
    public static final int REQ_TUMBLR_AUTH = 104;
    public static final int REQ_FILECHOOSER_FOR_TUMBLR = 105;

    public String mCameraPhotoPath;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageV21;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_FILECHOOSER:

                Uri[] results = null;

                // Check that the response is a good one
                if(resultCode == Activity.RESULT_OK) {
                    if(data == null) {
                        // If there is not data, then we may have taken a photo
                        if(mCameraPhotoPath != null) {
                            results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                        }
                    } else {
                        String dataString = data.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }

                if (mUploadMessageV21 != null) {
                    mUploadMessageV21.onReceiveValue(results);
                    mUploadMessageV21 = null;
                }
            default:
                break;
        }
    }

    public void sendEvent(String id, String name, String type){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && "LGE".equalsIgnoreCase(Build.BRAND)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && "LGE".equalsIgnoreCase(Build.BRAND)) {
            openOptionsMenu();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
