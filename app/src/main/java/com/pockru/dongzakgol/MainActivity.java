package com.pockru.dongzakgol;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.SubMenuBuilder;
import android.util.Log;
import android.view.SubMenu;
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
import android.widget.Toast;

import com.pockru.dongzakgol.model.Category;
import com.pockru.dongzakgol.module.imgur.helpers.DocumentHelper;
import com.pockru.dongzakgol.module.imgur.helpers.IntentHelper;
import com.pockru.dongzakgol.module.imgur.imgurmodel.ImageResponse;
import com.pockru.dongzakgol.module.imgur.imgurmodel.Upload;
import com.pockru.dongzakgol.module.imgur.services.UploadService;
import com.pockru.dongzakgol.webview.DZGWebView;
import com.pockru.dongzakgol.webview.DZGWebViewClient;
import com.pockru.dongzakgol.webview.UrlConts;

import java.io.File;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, DZGWebViewClient.InteractWithAvtivity {

    private DZGWebView mWebView;

    private String mMid = UrlConts.MAIN_MID;

    private FloatingActionButton mFabWrite;
    private FloatingActionButton mFabUploadImg;
    private SwipeRefreshLayout mRefreshLayout;
    private TextView mTvHeaderMsg;

    private NavigationView navigationView;

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

        mFabUploadImg = (FloatingActionButton) findViewById(R.id.fab_up);
        mFabUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 픽업 위해 갤러리 호출
                IntentHelper.chooseFileIntent(MainActivity.this);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mTvHeaderMsg = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_header_msg);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_main);
        mRefreshLayout.setColorSchemeColors(R.color.refresh_progress_1, R.color.refresh_progress_2);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

        mWebView = (DZGWebView) findViewById(R.id.webview);
        mWebView.loadUrl(UrlConts.getMainUrl());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mWebView.canGoBack()) {
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

        switch (item.getItemId()) {
            case R.id.nav_show_my_info:
                mWebView.loadUrl(UrlConts.getMyInfoUrlUrl(mMid));
                break;
            default:
                if (mList.size() > item.getItemId()) {
                    mWebView.loadUrl(UrlConts.MAIN_URL + mList.get(item.getItemId()).link);
                }
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_FILECHOOSER_FOR_IMGUR:
                if (resultCode != RESULT_OK) return;

                Uri returnUri = data.getData();
                String filePath = DocumentHelper.getPath(this, returnUri);
                //Safety check to prevent null pointer exception
                if (filePath == null || filePath.isEmpty()) return;
                File chosenFile = new File(filePath);

                Upload upload = createUpload(chosenFile);

                /*
                  Start upload
                 */
                new UploadService(this).Execute(upload, new Callback<ImageResponse>() {
                    @Override
                    public void success(ImageResponse imageResponse, Response response) {
                        Toast.makeText(getApplicationContext(), "이미지 업로드를 성공하였습니다.", Toast.LENGTH_LONG).show();
                        insertIntoImg(imageResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getApplicationContext(), "이미지 업로드를 실패하였습니다.", Toast.LENGTH_LONG).show();
                    }
                });

                break;

        }
    }

    private Upload createUpload(File image) {
        Upload upload = new Upload();

        upload.image = image;
        upload.title = "";
        upload.description = "";

        return upload;
    }

    private void insertIntoImg(ImageResponse imageResponse) {
        if (imageResponse == null) return;
        mWebView.loadJavaScript(UrlConts.insertImageJS(imageResponse.data.link));
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
                mFabUploadImg.show();
                break;
            default:
                fabControll(true);
                mFabUploadImg.hide();
                break;
        }
    }

    private void fabControll(boolean showWriteFab) {
        if (showWriteFab) {
            mFabWrite.show();
        } else {
            mFabWrite.hide();
        }
    }

    @Override
    public void notifyUrlLoadStart() {
        mRefreshLayout.setRefreshing(true);
    }

    List<Category> mList;

    @Override
    public void setCateList(final List<Category> list) {
        Log.i("test", "list : "+list);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (navigationView != null) {
                    SubMenu menu = navigationView.getMenu().getItem(0).getSubMenu();
                    int id = 0;
                    if (menu.size() == 0) {
                        mList = list;
                        for (Category category : list) {
                            menu.add(0, id, id, category.name);
                            id++;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void notifyUrlLoadFinish() {
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void notifyLogin(final boolean isLogin) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isLogin) {
                    mTvHeaderMsg.setText(R.string.inform_msg_logout);
                } else {
                    mTvHeaderMsg.setText(R.string.inform_msg_login);
                }
            }
        });
    }
}
