package com.pockru.dongzakgol;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.ValueCallback;

import com.pockru.dongzakgol.webview.UrlConts;

/**
 * Created by 래형 on 2015-12-24.
 */
public class BaseActivity extends AppCompatActivity {

    // onActivityResult값
    public static final int REQ_CODE_GET_PHOTO = 100;
    public static final int REQ_FILECHOOSER = 102;
    public static final int REQ_CODE_TUMBLR_AUTH = 104;
    public static final int REQ_CODE_DETAIL_ARTICLE = 103;
    public String mCameraPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageV21;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_FILECHOOSER:
//                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
//                if (mUploadMessage != null) {
//                    mUploadMessage.onReceiveValue(result);
//                    mUploadMessage = null;
//                }

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

                mUploadMessageV21.onReceiveValue(results);
                mUploadMessageV21 = null;

            default:
                break;
        }
    }
}
