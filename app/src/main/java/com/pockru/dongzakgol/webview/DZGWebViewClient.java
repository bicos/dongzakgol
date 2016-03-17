package com.pockru.dongzakgol.webview;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.pockru.dongzakgol.Const;
import com.pockru.dongzakgol.R;
import com.pockru.dongzakgol.model.Category;
import com.pockru.dongzakgol.util.UrlCheckUtils;

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

        UrlCheckUtils.checkUrl(url, mListener);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
    }

    @Override
    public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
        StringBuffer sb = new StringBuffer();
        switch(error.getPrimaryError())
        {
            case SslError.SSL_EXPIRED:
                sb.append("이 사이트의 보안 인증서는 신뢰할 수 없습니다.\n");
                break;
            case SslError.SSL_IDMISMATCH:
                sb.append("이 사이트의 보안 인증서는 신뢰할 수 없습니다.\n");
                break;
            case SslError.SSL_NOTYETVALID:
                sb.append("이 사이트의 보안 인증서는 신뢰할 수 없습니다.\n");
                break;
            case SslError.SSL_UNTRUSTED:
                sb.append("이 사이트의 보안 인증서는 신뢰할 수 없습니다.\n");
                break;
            default:
                sb.append("보안 인증서에 오류가 있습니다.\n");
                break;

        }
        sb.append("계속 진행하시겠습니까?");

        new AlertDialog.Builder(view.getContext())
                .setMessage(sb.toString())
                .setPositiveButton("진행", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                })
                .show();
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
        void notifyUrlLoadStart();
    }
}
