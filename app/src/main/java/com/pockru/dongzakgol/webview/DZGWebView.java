package com.pockru.dongzakgol.webview;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pockru.dongzakgol.BuildConfig;
import com.pockru.dongzakgol.Const;
import com.pockru.dongzakgol.MainActivity;
import com.pockru.dongzakgol.R;
import com.pockru.dongzakgol.util.DialogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 래형 on 2015-12-24.
 */
public class DZGWebView extends WebView {

    public DZGWebView(Context context) {
        super(context);
        init();
    }

    public DZGWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private HitTestResult mResult;

    private void init() {

        setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(1);

                final HitTestResult result = getHitTestResult();

                int type = result.getType();

                if (type == HitTestResult.IMAGE_TYPE || type == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    DialogUtil.showListDialog(getContext(), result.getExtra(), getResources().getStringArray(R.array.photo_act_list),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0: {
                                            int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                            if (permission != PackageManager.PERMISSION_GRANTED) {
                                                mResult = result;
                                                // We don't have permission so prompt the user
                                                ActivityCompat.requestPermissions(
                                                        (Activity) getContext(),
                                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                        MainActivity.REQUEST_WRITE_EXTERNAL_STORAGE
                                                );
                                            } else {
                                                imageDownload(result);
                                            }
                                            break;
                                        }
                                        case 1:
                                            saveUrl(result.getExtra());
                                            break;
                                        default:
                                            break;
                                    }
                                }

                            });
                } else if (type == HitTestResult.SRC_ANCHOR_TYPE) {
                    DialogUtil.showListDialog(getContext(), result.getExtra(), getResources().getStringArray(R.array.link_list),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            saveUrl(result.getExtra());
                                        default:
                                            break;
                                    }
                                }

                            });
                }

                return false;
            }
        });

        getSettings().setSupportZoom(true);
        getSettings().setBuiltInZoomControls(true);
        getSettings().setPluginState(WebSettings.PluginState.ON);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSettings().setDisplayZoomControls(false);
        }
        // mWebView.getSettings().setSupportMultipleWindows(true);
        // mWebView.getSettings().setLoadWithOverviewMode(true);
        getSettings().setUseWideViewPort(true);

        // Setting Local Storage
        getSettings().setDatabaseEnabled(true);
        getSettings().setDomStorageEnabled(true);
//        String databasePath = getContext().getDir("database", Context.MODE_PRIVATE).getPath();
//        // Log.i(TAG, "databasePath : "+databasePath);
//        getSettings().setDatabasePath(databasePath);

        getSettings().setUserAgentString(getSettings().getUserAgentString() + "_" + UrlConts.UA_APP+"_"+ BuildConfig.VERSION_CODE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW );
        }

        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }

//        if (getContext() instanceof DZGWebViewClient.InteractWithAvtivity) {
//            addJavascriptInterface(new JSBridge((DZGWebViewClient.InteractWithAvtivity) getContext()),
//                    JSBridge.TAG);
//        }
        if (getContext() instanceof DZGWebViewClient.InteractWithAvtivity) {
            addJavascriptInterface(new DzgolBridge((DZGWebViewClient.InteractWithAvtivity) getContext()), DzgolBridge.TAG);
        }

        setDownloadListener(new CustomDownloadListener());

        setWebViewClient(new DZGWebViewClient(getContext()) {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        setWebChromeClient(new DZGWebViewChromeClient(getContext()) {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                if (mProgressBar != null) {
                    if(newProgress < 100) {
                        mProgressBar.setProgress(newProgress);
                    } else {
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void imageDownload(HitTestResult result) {
        DownloadManager manager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        String url = result.getExtra();
        String fileName = createFileName(url);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("사진 다운로드");
        request.setDescription(url);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, fileName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        manager.enqueue(request);
    }

    private String createFileName(String url) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String ext = MimeTypeMap.getFileExtensionFromUrl(url);
        ext = TextUtils.isEmpty(ext) ? "png" : ext;
        String fileName = Const.IMG_PREFIX_NAME + "_" + timeStamp + "." + ext;
        return fileName;
    }

    private void saveUrl(String extra) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ClipboardManager mgr = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData date = ClipData.newPlainText(extra, extra);
            mgr.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {

                @Override
                public void onPrimaryClipChanged() {
                    Toast.makeText(getContext().getApplicationContext(), getContext().getString(R.string.msg_save_link_url), Toast.LENGTH_SHORT).show();
                }
            });
            mgr.setPrimaryClip(date);
        } else {
            android.text.ClipboardManager mgr = (android.text.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            mgr.setText(extra);
            Toast.makeText(getContext().getApplicationContext(), getContext().getString(R.string.msg_save_link_url), Toast.LENGTH_SHORT).show();
        }
    }

    //#btn_more > ul
    public void loadJavaScript(String javascript) {
        loadJavaScript(javascript, null);
    }

    public void loadJavaScript(String javascript, ValueCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(javascript, callback);
        } else {
            loadUrl(javascript);
        }
    }

    private String referer = UrlConts.MAIN_URL;

    // accept-encoding:gzip, deflate, sdch
    // referer:http://www.dzgol.net/
    private Map<String, String> getDefaultAdditionalHeader() {
        Map<String, String> additionalHeader = new HashMap<>();
        additionalHeader.put("accept-encoding ", "gzip, deflate, sdch");
        additionalHeader.put("Referer", referer);
        additionalHeader.put("Upgrade-Insecure-Requests", "1");
        return additionalHeader;
    }

    @Override
    public void loadUrl(String url) {
//        loadUrl(url, getDefaultAdditionalHeader());
        super.loadUrl(url);
        referer = url;
    }

    private ProgressBar mProgressBar;

    public void setProgressBar(ProgressBar progressBar) {
        mProgressBar = progressBar;
    }

    public static interface PageScrollState {
        void onStateUp();

        void onStateDown();
    }

    private PageScrollState mPageScrollState;

    public void setOnPageScrollSateListener(PageScrollState listener) {
        mPageScrollState = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mPageScrollState != null) {
            if (t - oldt < 0) {
                mPageScrollState.onStateUp();
            } else if (t - oldt > 0) {
                mPageScrollState.onStateDown();
            }
        }
    }

    public void saveImg(){
        int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            imageDownload(mResult);
        }

        mResult = null;
    }

    class CustomDownloadListener implements DownloadListener {
        public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimetype, final long contentLength) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            getContext().startActivity(intent);
        }
    }

    public static class DzgolBridge {
        public static final String TAG = DzgolBridge.class.getSimpleName();

        DZGWebViewClient.InteractWithAvtivity mListener;

        public DzgolBridge(DZGWebViewClient.InteractWithAvtivity listener) {
            mListener = listener;
        }

        @JavascriptInterface
        public void login(String msg) {
            mListener.onLogin(msg);
        }

        @JavascriptInterface
        public void logout(String msg) {
            mListener.onLogout(msg);
        }

        @JavascriptInterface
        public void isShowAd(boolean isShowAd) {
            mListener.isShowAd(isShowAd);
        }
    }

    public static class JSBridge {

        public static final String TAG = JSBridge.class.getSimpleName();

        DZGWebViewClient.InteractWithAvtivity mListener;

        public JSBridge(DZGWebViewClient.InteractWithAvtivity listener) {
            mListener = listener;
        }

        @JavascriptInterface
        public void print(String data, String flag) {
            if (data == null) return;
//            switch (flag) {
//                case Const.FLAG_CHECK_LOGIN:
//                    if (data.contains(UrlConts.ACT_LOGIN)) {
//                        mListener.notifyLogin(false);
//                    } else {
//                        mListener.notifyLogin(true);
//                    }
//                    break;
//                case Const.FLAG_MAIN_LIST:
//                    Document doc = Jsoup.parse(data);
//                    if (doc != null) {
//                        try {
//                            Elements elements = doc.select("#btn_more > ul").get(0).getElementsByTag("a");
//                            List<Category> list = new ArrayList<>();
//                            Element element;
//                            for (int i = 0; i < elements.size(); i++) {
//                                element = elements.get(i);
//                                Category cate = new Category(element.text(), element.attr("href"));
//                                list.add(cate);
//                            }
//
//                            if (list.size() > 0) {
//                                mListener.setCateList(list);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    break;
//            }
        }
    }
}
