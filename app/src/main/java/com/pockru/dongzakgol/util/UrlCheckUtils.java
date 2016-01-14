package com.pockru.dongzakgol.util;

import android.net.Uri;
import android.text.TextUtils;

import com.pockru.dongzakgol.webview.DZGWebViewClient;
import com.pockru.dongzakgol.webview.UrlConts;

import java.util.List;

/**
 * Created by 래형 on 2015-12-29.
 */
public class UrlCheckUtils {

    public static final void checkUrl(String url, DZGWebViewClient.InteractWithAvtivity mListener) {
        if (url != null && mListener != null) {
            Uri uri = Uri.parse(url);
            if (uri != null && uri.isOpaque() == false) {
                List<String> segments = uri.getPathSegments();

                if (segments != null && segments.size() > 0) {
                    if (segments.get(0) != null) {
                        String mid = segments.get(0);

                        if (TextUtils.isEmpty(mid) == false) {
                            mListener.setMid(mid);
                        }
                    }
                }

                String mid = uri.getQueryParameter(UrlConts.PARAM_MID);
                if (TextUtils.isEmpty(mid) == false) {
                    mListener.setMid(mid);
                }

                String act = uri.getQueryParameter(UrlConts.PARAM_ACT);
                if (TextUtils.isEmpty(act) == false) {
                    mListener.setAct(act);
                } else {
                    mListener.setAct(""); // set act nothing
                }
            }
        }
    }
}
