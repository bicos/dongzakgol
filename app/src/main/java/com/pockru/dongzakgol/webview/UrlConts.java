package com.pockru.dongzakgol.webview;

import android.net.Uri;

import java.util.Map;

/**
 * Created by 래형 on 2015-12-24.
 */
public class UrlConts {

    public static final String MAIN_URL = "http://sungyun4463.cafe24.com";
    public static final String MAIN_PATH = "index.php";

    public static final String ACT_WRITE = "dispBoardWrite";

    public static final String getWriteUrl(String mid) {
        Uri uri = Uri.parse(MAIN_URL);
        uri = uri.buildUpon()
                .appendPath(MAIN_PATH)
                .appendQueryParameter("mid", mid)
                .appendQueryParameter("act", ACT_WRITE).build();
        return uri.toString();
    }
}
