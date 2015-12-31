package com.pockru.dongzakgol.module.tumblr.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import com.pockru.dongzakgol.R;
import com.pockru.dongzakgol.util.Preference;
import com.pockru.dongzakgol.webview.DZGWebView;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

/**
 * @author bicos
 * 
 */
public class TumblrOAuthActivity extends Activity {

	private static final String TAG = "TumblrOAuthActivity";

	public static final String CONSUMER_ID = "wNaVX9q6ysZNKAlRmB6ZH5WHWlcgxzFncKLYHX85qYKWI8zhDp";
	public static final String CONSUMER_SECRET = "OMIxtBwFpjYearRwVZDSTFDOEIJ86J6TDnyeIA5rGrLGRAbjlD";

	private static final int MSG_RETRIEVE_REQ_TOKEN = 0;
	private static final int MSG_RETRIEVE_ACCESS_TOKEN = 1;

	public static final String REQUEST_URL = "https://www.tumblr.com/oauth/request_token";
	public static final String ACCESS_URL = "https://www.tumblr.com/oauth/access_token";
	public static final String AUTHORIZE_URL = "https://www.tumblr.com/oauth/authorize";

	public static final String OAUTH_CALLBACK_SCHEME = "dzgol";
	public static final String OAUTH_CALLBACK_HOST = "";
	public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

	private CommonsHttpOAuthConsumer consumer;
	private CommonsHttpOAuthProvider provider;

	private String token, secret, authURL, verifier;

	private ViewGroup mContainer;
	private DZGWebView mWebView;
	private ImageButton mBtnClose;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RETRIEVE_REQ_TOKEN:
				Log.i(TAG, "MSG_REQ_TOKEN : " + msg.obj);
				mWebView.loadUrl((String) msg.obj);
				break;
			case MSG_RETRIEVE_ACCESS_TOKEN:
				String token = consumer.getToken();
				String secret = consumer.getTokenSecret();
				if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(secret)) {
					Preference.setTumblrToken(TumblrOAuthActivity.this, token);
					Preference.setTumblrSecret(TumblrOAuthActivity.this, secret);
					Toast.makeText(TumblrOAuthActivity.this, getString(R.string.toast_msg_connect_success, "tumblr"), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(TumblrOAuthActivity.this, getString(R.string.error_msg_cant_interlock, "tumblr"), Toast.LENGTH_SHORT).show();
				}
				
				setResult(RESULT_OK);
				finish();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mWebView.onResume();
		}
	}

	@Override
	protected void onPause() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mWebView.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (mWebView != null) {
			if (mContainer != null) {
				mContainer.removeView(mWebView);				
			}
			mWebView.removeAllViews();
			mWebView.destroy();
		}
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actvity_tumblr_auth);

		consumer = new CommonsHttpOAuthConsumer(CONSUMER_ID, CONSUMER_SECRET);
		provider = new CommonsHttpOAuthProvider(REQUEST_URL, ACCESS_URL, AUTHORIZE_URL);

		token = Preference.getTumblrToken(this);
		secret = Preference.getTumblrSecret(this);

		mContainer = (ViewGroup) findViewById(R.id.containerWebView);
		mWebView = (DZGWebView) findViewById(R.id.webView);

		mWebView.setWebViewClient(new CallBack());

		if (TextUtils.isEmpty(token) || TextUtils.isEmpty(secret)) {
			setAuthURL();
		} else {
			Toast.makeText(this, getString(R.string.error_msg_already_get_token, "tumblr"), Toast.LENGTH_SHORT).show();
			finish();
			setResult(RESULT_OK);
		}
		
		mBtnClose = (ImageButton) findViewById(R.id.btn_close);
		mBtnClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void setAuthURL() {
		new Thread() {
			@Override
			public void run() {
				try {
					authURL = provider.retrieveRequestToken(consumer, OAUTH_CALLBACK_URL);
				} catch (OAuthMessageSignerException | OAuthNotAuthorizedException | OAuthExpectationFailedException | OAuthCommunicationException e) {
					e.printStackTrace();
				}
				Message msg = mHandler.obtainMessage(MSG_RETRIEVE_REQ_TOKEN, authURL);
				mHandler.sendMessage(msg);

				super.run();
			}
		}.start();
	}

	private class CallBack extends WebViewClient {

		ProgressDialog dialog = new ProgressDialog(TumblrOAuthActivity.this);

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains("oauth_verifier")) {
				Uri uri = Uri.parse(url);
				verifier = uri.getQueryParameter("oauth_verifier");
				new Thread() {
					public void run() {
						try {
							provider.retrieveAccessToken(consumer, verifier);
						} catch (OAuthMessageSignerException | OAuthNotAuthorizedException | OAuthExpectationFailedException | OAuthCommunicationException e) {
							e.printStackTrace();
						}
						mHandler.sendEmptyMessage(MSG_RETRIEVE_ACCESS_TOKEN);
					};
				}.start();
				return true;
			}
			
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			dialog.setMessage("Loading page . . .");
			if (!dialog.isShowing())
				dialog.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			dialog.dismiss();
		}
	}

}
