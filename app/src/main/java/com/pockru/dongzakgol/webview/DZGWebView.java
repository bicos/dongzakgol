package com.pockru.dongzakgol.webview;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.pockru.dongzakgol.Const;
import com.pockru.dongzakgol.R;
import com.pockru.dongzakgol.util.DialogUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
                                        case 0:
                                            imageDownload(result);
                                            break;
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
        getSettings().setDisplayZoomControls(false);
        // mWebView.getSettings().setSupportMultipleWindows(true);

        // mWebView.getSettings().setLoadWithOverviewMode(true);
        getSettings().setUseWideViewPort(true);

        // Setting Local Storage
        getSettings().setDatabaseEnabled(true);
        getSettings().setDomStorageEnabled(true);
        String databasePath = getContext().getDir("database", Context.MODE_PRIVATE).getPath();
        // Log.i(TAG, "databasePath : "+databasePath);
        getSettings().setDatabasePath(databasePath);

        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }

        setDownloadListener(new CustomDownloadListener());

        setWebViewClient(new DZGWebViewClient(getContext()));
        setWebChromeClient(new DZGWebViewChromeClient(getContext()));
    }

    private void imageDownload(HitTestResult result){
        DownloadManager manager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        String url = result.getExtra();
        String fileName = createFileName(url);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("사진 다운로드");
        request.setDescription(url);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        manager.enqueue(request);
    }

    private String createFileName(String url){
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String ext = MimeTypeMap.getFileExtensionFromUrl(url);
        ext = TextUtils.isEmpty(ext) ? "png" : ext;
        String fileName = Const.IMG_PREFIX_NAME + "_" + timeStamp +"."+ ext;
        return fileName;
    }

    private void saveUrl(String extra) {
        ClipboardManager mgr = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData date = ClipData.newPlainText(extra, extra);
        mgr.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {

            @Override
            public void onPrimaryClipChanged() {
                Toast.makeText(getContext(), getContext().getString(R.string.msg_save_link_url), Toast.LENGTH_SHORT).show();
            }
        });
        mgr.setPrimaryClip(date);
    }

    class CustomDownloadListener implements DownloadListener {
        public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimetype, final long contentLength) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            getContext().startActivity(intent);
        }
    }

    class JIFace {
        @JavascriptInterface
        public void print(String data) {
            data = "<html>" + data + "</html>";
            Document doc = Jsoup.parse(data);

        }
    }
}
