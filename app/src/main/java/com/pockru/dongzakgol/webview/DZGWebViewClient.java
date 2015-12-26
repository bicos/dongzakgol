package com.pockru.dongzakgol.webview;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.pockru.dongzakgol.Const;
import com.pockru.dongzakgol.R;
import com.pockru.dongzakgol.model.Category;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by 래형 on 2015-12-24.
 */
public class DZGWebViewClient extends WebViewClient {

    private Context mContext;
    private InteractWithAvtivity mListener;

    public DZGWebViewClient(Context context) {
        mContext = context;
        if (mContext instanceof InteractWithAvtivity) {
            mListener = (InteractWithAvtivity) mContext;
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (mListener != null) {
            mListener.notifyUrlLoadStart();
        }

        if (mListener != null) {
            if (url != null) {
                Uri uri = Uri.parse(url);
                if (uri != null && uri.isOpaque() == false) {
                    String mid = uri.getQueryParameter(UrlConts.PARAM_MID);
                    String act = uri.getQueryParameter(UrlConts.PARAM_ACT);
                    if (TextUtils.isEmpty(mid) == false) {
                        mListener.setMid(mid);
                    }
                    if (TextUtils.isEmpty(act) == false) {
                        mListener.setAct(act);
                    }
                }
            }
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i("test", "shouldOverrideUrlLoading : " + url);
        Uri uri = Uri.parse(url);
        if (uri != null && (uri.getScheme().equals("http") || uri.getScheme().equals("https") || uri.getScheme().equals("javascript"))) {
            if (uri.getHost().contains(Uri.parse(UrlConts.MAIN_URL).getHost())) {
                view.loadUrl(url);
                return true;
            } else {
                return sendOutside(url);
            }
        } else {
            return sendOutside(url);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (mListener != null) {
            mListener.notifyUrlLoadFinish();
        }
        if (view instanceof DZGWebView) {
            ((DZGWebView)view).loadJavaScript(UrlConts.getHtml(Const.FLAG_CHECK_LOGIN));
        }
        if (url.contains(UrlConts.MAIN_MID)) {
            ((DZGWebView)view).loadJavaScript(UrlConts.getHtml(Const.FLAG_MAIN_LIST));
        }
    }

    private boolean sendOutside(String url) {
        Intent intent = null;
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            mContext.startActivity(intent);
        } catch (URISyntaxException e) {
            handleURISyntaxException(e);
            e.printStackTrace();
        } catch (ActivityNotFoundException e) {
            handleActivityNotFoundException(e, intent);
            e.printStackTrace();
        }
        return true;
    }

    private void handleURISyntaxException(URISyntaxException e) {
        Toast.makeText(mContext, mContext.getString(R.string.msg_illegal_uri_syntax), Toast.LENGTH_LONG).show();
    }

    private void handleActivityNotFoundException(ActivityNotFoundException e, Intent intent) {
        String packageName = intent.getPackage();
        if (packageName != null && !packageName.equals("")) {
            Toast.makeText(mContext, mContext.getString(R.string.msg_not_supported_url), Toast.LENGTH_LONG).show();
            Intent goMarket = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getString(R.string.url_go_market) + packageName));
            mContext.startActivity(goMarket);
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.msg_not_supported_url), Toast.LENGTH_LONG).show();
        }
    }

    public interface InteractWithAvtivity {

        void setMid(String mid);
        void setAct(String act);
        void notifyUrlLoadFinish();
        void notifyLogin(boolean isLogin);
        void notifyUrlLoadStart();
        void setCateList(List<Category> list);
    }
}