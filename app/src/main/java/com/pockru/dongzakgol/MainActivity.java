package com.pockru.dongzakgol;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.TextView;

import com.pockru.dongzakgol.webview.DZGWebView;
import com.pockru.dongzakgol.webview.DZGWebViewClient;
import com.pockru.dongzakgol.webview.UrlConts;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, DZGWebViewClient.InteractWithAvtivity {

    private DZGWebView mWebView;

    private String mMid;

    private FloatingActionButton mFabWrite;
    private FloatingActionButton mFabUp;
    private SwipeRefreshLayout mRefreshLayout;
    private TextView mTvHeaderMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFabWrite = (FloatingActionButton) findViewById(R.id.fab_write);
        mFabWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl(UrlConts.getWriteUrl(mMid));
            }
        });

        mFabUp = (FloatingActionButton) findViewById(R.id.fab_up);
        mFabUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mWebView.setScrollY(0);
                mWebView.loadUrl(UrlConts.insertImageJS("http://imgnews.naver.net/image/upload/item/2015/12/25/172402405_1.jpg?type=f270_166"));
            }
        });

        // <img src= "http://i.imgur.com/Q1APjZv.png" width ="100%">

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mTvHeaderMsg = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_header_msg);
        mTvHeaderMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTvHeaderMsg.isEnabled()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mWebView.evaluateJavascript(UrlConts.getLoginUrl(mWebView.getUrl()), new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {

                            }
                        });
                    } else {
                        mWebView.loadUrl(UrlConts.getLoginUrl(mWebView.getUrl()));
                    }
                }
            }
        });

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_main);
        mRefreshLayout.setColorSchemeColors(R.color.refresh_progress_1, R.color.refresh_progress_2);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

        mWebView = (DZGWebView) findViewById(R.id.webview);
        mWebView.loadUrl(UrlConts.MAIN_URL);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }

    /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @Override
    public void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private static final String PREFIX_BOARD = "board";

    @Override
    public void setMid(String mid) {
        mMid = mid;
        if (mid.startsWith(PREFIX_BOARD)) {
            fabControll(true);
        } else {
            fabControll(false);
        }
    }

    @Override
    public void setAct(String act) {
        switch (act) {
            case UrlConts.ACT_WRITE:
                fabControll(false);

                // test

                break;
            default:
                fabControll(true);
                break;
        }
    }

    private void fabControll(boolean showWriteFab) {
        if (showWriteFab) {
            mFabUp.animate()
                    .translationY(-(mFabWrite.getMeasuredHeight() +
                            getResources().getDimensionPixelOffset(R.dimen.fab_margin)))
                    .start();
            mFabWrite.show();
        } else {
            mFabUp.animate().
                    translationY(0)
                    .start();
            mFabWrite.hide();
        }
    }

    @Override
    public void notifyUrlLoadFinish() {
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void notifyLogin(boolean isLogin) {
        mTvHeaderMsg.setEnabled(!isLogin);
    }

    @Override
    public void nofityImgLinkComponentOpen() {
        // TODO test
//        WebView childWebView = mWebView.getChildWebView();
        mWebView.loadUrl("javascript:document.querySelector(\"#editor\").innerHTML = '<img src =\"http://i.imgur.com/Q1APjZv.png\">';");
    }
}
