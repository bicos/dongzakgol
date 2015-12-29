package com.pockru.dongzakgol.util;

import android.net.Uri;
import android.text.TextUtils;

import com.pockru.dongzakgol.webview.DZGWebViewClient;
import com.pockru.dongzakgol.webview.UrlConts;

/**
 * Created by 래형 on 2015-12-29.
 */
public class UrlCheckUtils {

    public static final void checkUrl(String url, DZGWebViewClient.InteractWithAvtivity mListener) {
        if (url != null && mListener != null) {
            Uri uri = Uri.parse(url);
            if (uri != null && uri.isOpaque() == false) {
                String mid = uri.getQueryParameter(UrlConts.PARAM_MID);
                String act = uri.getQueryParameter(UrlConts.PARAM_ACT);

                if (TextUtils.isEmpty(mid) == false) {
                    mListener.setMid(mid);
                }

                if (TextUtils.isEmpty(act) == false) {
                    mListener.setAct(act);
                } else {
                    mListener.setAct(""); // set act nothing
                }
            }
        }
    }
}
